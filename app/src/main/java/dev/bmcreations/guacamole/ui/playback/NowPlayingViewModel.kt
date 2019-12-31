package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.extensions.randomOrNull
import dev.bmcreations.guacamole.media.MediaBrowserCallback
import dev.bmcreations.guacamole.media.MediaBrowserConnection
import dev.bmcreations.guacamole.media.MediaSessionManager
import dev.bmcreations.guacamole.media.playbackStateString
import dev.bmcreations.guacamole.models.apple.Container
import dev.bmcreations.guacamole.models.apple.TrackEntity
import dev.bmcreations.guacamole.viewmodel.SingleLiveEvent
import dev.bmcreations.guacamole.operator.inTime
import dev.bmcreations.guacamole.extensions.mediaId
import dev.bmcreations.guacamole.media.MusicQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class NowPlayingViewModel private constructor(
    val context: Context, val queue: MusicQueue
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    val selectedTrack = MutableLiveData<TrackEntity?>()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    private var initializationFailedJob: Job? = null

    private val mediaBrowserConnection: MediaBrowserConnection? by lazy {
        MediaBrowserConnection(
            context
        )
    }

    private val mediaBrowserCallback by lazy {
        MediaBrowserCallback(
            mediaBrowserConnection
        ) { extras, i ->
            info { "playback state change -- state=${i.playbackStateString}" }
            when (i) {
                PlaybackStateCompat.STATE_BUFFERING -> onTrackBuffering()
                PlaybackStateCompat.STATE_PLAYING -> onMusicStarted()
                PlaybackStateCompat.STATE_PAUSED -> onMusicPaused()
                PlaybackStateCompat.STATE_STOPPED -> onMusicStopped()
            }

            updateCurrentTrackFromCallback(extras?.get(MediaSessionManager.EXTRA_CURRENT_TRACK) as? TrackEntity)
        }
    }

    private fun updateCurrentTrackFromCallback(track: TrackEntity?) {
        track?.let {
            if (selectedTrack.value != it) {
                selectedTrack.value = it
            }
        }
    }

    sealed class State {
        object Playing : State()
        object Paused : State()
        object Initializing : State()
        object Uninitialized : State()
        object InitializationFailed : State()
    }

    init {
        selectedTrack.value = null
        playState.value = State.Uninitialized

        mediaBrowserConnection?.onStart()
        mediaBrowserConnection?.registerCallback(mediaBrowserCallback)
    }

    override fun onCleared() {
        super.onCleared()
        mediaBrowserConnection?.unregisterCallback(mediaBrowserCallback)
        mediaBrowserConnection?.onStop()
    }

    companion object Factory {
        fun create(context: Context, queue: MusicQueue): NowPlayingViewModel {
            return NowPlayingViewModel(context, queue)
        }
    }

    private fun loadCollection(collection: Container?, position: Int = -1) {
        collection?.let {
            val newQueue = when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_NONE -> {
                    it.trackList?.subList(
                        position.coerceAtLeast(0),
                        it.trackList?.size ?: 0
                    ) ?: emptyList()
                }
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    it.trackList?.let { tracks ->
                        listOf(tracks[position.coerceAtLeast(0)])
                    }
                }
                else -> {
                    it.trackList
                }
            }

            queue.updateQueue(newQueue?.map { t ->
                TrackEntity(
                    t,
                    collection
                )
            })
        }
    }

    fun playAlbum(collection: Container?) {
        shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE
        loadCollection(collection)
        queue.onTrackSelected()
        play(queue.tracks?.firstOrNull())
    }

    fun play(track: TrackEntity?) {
        loadCollection(track?.container, track?.container?.trackList?.indexOf(track.track) ?: -1)
        queue.onTrackSelected(track)
        track?.let { playInternal(it) }
    }

    fun shuffleAlbum(collection: Container?) {
        shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_ALL
        loadCollection(collection)
        queue.onTrackSelected()
        play(queue.tracks?.randomOrNull())
    }

    var repeatMode: Int
        get() = mediaBrowserConnection?.mediaController?.repeatMode
            ?: PlaybackStateCompat.REPEAT_MODE_NONE
        set(value) {
            mediaBrowserConnection?.mediaController?.transportControls?.setRepeatMode(value)
        }

    var shuffleMode: Int
        get() = mediaBrowserConnection?.mediaController?.shuffleMode
            ?: PlaybackStateCompat.SHUFFLE_MODE_NONE
        set(value) {
            mediaBrowserConnection?.mediaController?.transportControls?.setShuffleMode(value)
        }

    private fun playInternal(track: TrackEntity) {
        playState.postValue(State.Initializing)
        selectedTrack.postValue(track)
        mediaBrowserConnection?.mediaController?.let { mc ->
            val extras = Bundle().apply {
                this.putString(
                    MediaSessionManager.EXTRA_QUEUE_IDENTIFIER,
                    track?.toMetadata()?.mediaId
                )
            }
            mc.sendCommand(
                MediaSessionManager.COMMAND_SWAP_QUEUE,
                extras,
                object : ResultReceiver(null) {
                    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                        if (resultCode == MediaSessionManager.RESULT_ADD_QUEUE_ITEMS) {
                            mc.queue.clear()
                            queue.tracks?.forEach { track ->
                                track.toMetadata().mediaId?.let {
                                    mc.addQueueItem(track.toMetadata().description)
                                }
                            }
                            mc.transportControls?.prepare()
                        }

                        track.let {
                            mc.transportControls?.playFromMediaId(it.toMetadata().mediaId, null)
                            waitForMusicPlayback()
                        }
                    }
                })
        }
    }

    fun playPause() {
        playState.value?.let {
            when (it) {
                is State.Playing -> {
                    mediaBrowserConnection?.mediaController?.transportControls?.pause()
                    playState.value = State.Paused
                }
                is State.Paused -> {
                    mediaBrowserConnection?.mediaController?.transportControls?.play()
                    waitForMusicPlayback()
                    playState.value = State.Playing
                }
                is State.Uninitialized,
                is State.InitializationFailed -> {
                    mediaBrowserConnection?.mediaController?.transportControls?.play()
                    waitForMusicPlayback()
                    playState.value = State.Initializing
                }
            }
        }
    }

    private fun waitForMusicPlayback() {
        initializationFailedJob =
            inTime(offset = 4000) {
                playState.value = State.InitializationFailed
            }
        initializationFailedJob?.start()
    }

    fun onMusicStarted() {
        initializationFailedJob?.cancel()
        playState.value = State.Playing
    }

    fun onTrackBuffering() {
        initializationFailedJob?.cancel()
    }

    fun onMusicPaused() {
        playState.value = State.Paused
    }

    fun onMusicStopped() {
        playState.value = State.Paused
    }
}
