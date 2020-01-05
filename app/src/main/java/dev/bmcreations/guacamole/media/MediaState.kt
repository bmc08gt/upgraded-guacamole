package dev.bmcreations.guacamole.media

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import dev.bmcreations.guacamole.extensions.mediaId
import dev.bmcreations.guacamole.extensions.randomOrNull
import dev.bmcreations.guacamole.models.apple.Container
import dev.bmcreations.guacamole.models.apple.TrackEntity
import dev.bmcreations.guacamole.operator.inTime
import dev.bmcreations.guacamole.viewmodel.SingleLiveEvent
import kotlinx.coroutines.Job
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

interface MediaStateLifecycleEvents {
    fun onStart()
    fun onStop()
}

object MediaStateLifecycleObserver : LifecycleEventObserver {
    private var events: MediaStateLifecycleEvents? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> events?.onStart()
            Lifecycle.Event.ON_STOP -> events?.onStop()
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> {}
        }
    }

    fun registerEventListener(events: MediaStateLifecycleEvents) {
        this.events = events
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }
}

class MediaState(
    val context: Context,
    val queue: MusicQueue
) : AnkoLogger, MediaStateLifecycleEvents {
    val currentTrack = MutableLiveData<TrackEntity?>()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    init {
        currentTrack.value = null
        playState.value = State.Uninitialized

        MediaStateLifecycleObserver.registerEventListener(this)
    }

    private var initializationFailedJob: Job? = null

    private val mediaBrowserConnection: MediaBrowserConnection by lazy {
        MediaBrowserConnection(context)
    }

    private val mediaBrowserCallback by lazy {
        MediaBrowserCallback(mediaBrowserConnection) { extras, i ->
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

    private fun onMusicStarted() {
        mediaBrowserConnection.mediaController?.transportControls?.play()
        initializationFailedJob?.cancel()
        playState.value = State.Playing
    }

    private fun onTrackBuffering() {
        initializationFailedJob?.cancel()
        playState.value = State.Buffering
    }

    private fun onMusicPaused() {
        playState.value = State.Paused
    }

    fun pause() {
        mediaBrowserConnection.mediaController?.transportControls?.pause()
    }

    fun play() {
        mediaBrowserConnection.mediaController?.transportControls?.play()
        waitForMusicPlayback()
    }

    private fun onMusicStopped() {
        playState.value = State.Stopped
    }

    private fun waitForMusicPlayback() {
        initializationFailedJob =
            inTime(offset = 4000) {
                playState.value = State.InitializationFailed
            }
        initializationFailedJob?.start()
    }

    override fun onStart() {
        mediaBrowserConnection.onStart()
        mediaBrowserConnection.registerCallback(mediaBrowserCallback)
    }

    override fun onStop() {
        mediaBrowserConnection.unregisterCallback(mediaBrowserCallback)
        mediaBrowserConnection.onStop()
    }

    var repeatMode: Int
        get() = mediaBrowserConnection.mediaController?.repeatMode
            ?: PlaybackStateCompat.REPEAT_MODE_NONE
        set(value) {
            mediaBrowserConnection.mediaController?.transportControls?.setRepeatMode(value)
        }

    var shuffleMode: Int
        get() = mediaBrowserConnection.mediaController?.shuffleMode
            ?: PlaybackStateCompat.SHUFFLE_MODE_NONE
        set(value) {
            mediaBrowserConnection.mediaController?.transportControls?.setShuffleMode(value)
        }

    private fun loadCollection(collection: Container?, position: Int = -1) {
        collection?.let {
            val newQueue = when (repeatMode) {
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
                TrackEntity(t, collection)
            })
        }
    }

    fun shuffle(collection: Container?) {
        shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_ALL
        loadCollection(collection)
        queue.onTrackSelected()
        playInternal(queue.tracks?.randomOrNull())
    }

    fun play(collection: Container?) {
        shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE
        loadCollection(collection)
        queue.onTrackSelected()
        playInternal(queue.tracks?.firstOrNull())
    }

    fun play(track: TrackEntity?) {
        loadCollection(track?.container, track?.container?.trackList?.indexOf(track.track) ?: -1)
        queue.onTrackSelected(track)
        track?.let { playInternal(it) }
    }

    private fun playInternal(track: TrackEntity?) {
        track?.let {
            playState.postValue(State.Initializing)
            mediaBrowserConnection.mediaController?.let { mc ->
                val extras = Bundle().apply {
                    this.putString(MediaSessionManager.EXTRA_QUEUE_IDENTIFIER, it.toMetadata().mediaId)
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

                            mc.transportControls?.playFromMediaId(it.toMetadata().mediaId, null)
                            waitForMusicPlayback()
                        }
                    })
            }
        }
    }

    private fun updateCurrentTrackFromCallback(track: TrackEntity?) {
        track?.let {
            if (currentTrack.value != it) {
                currentTrack.value = it
            }
        }
    }

    fun handlePlayStateChange() {
        when (playState.value) {
            is State.Playing -> pause()
            is State.Paused -> play()
            is State.Uninitialized,
            is State.InitializationFailed -> {
                play()
                playState.value = State.Initializing
            }
        }
    }
}
