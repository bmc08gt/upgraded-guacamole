package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.musickit.networking.extensions.inTime
import dev.bmcreations.guacamole.media.MediaSessionManager
import dev.bmcreations.guacamole.viewmodel.SingleLiveEvent
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import dev.bmcreations.musickit.networking.extensions.mediaId
import dev.bmcreations.musickit.networking.extensions.randomOrNull
import kotlinx.coroutines.Job
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class NowPlayingViewModel private constructor(val context: Context, val music: MusicRepository): ViewModel(), AnkoLogger {

    val selectedTrack = MutableLiveData<TrackEntity?>()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    private var initializationFailedJob : Job? = null

    private val mediaBrowserConnection: MediaBrowserConnection? by lazy { MediaBrowserConnection(context) }

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

    private fun updateCurrentTrackFromCallback(track: TrackEntity?) {
        track?.let {
            if (selectedTrack.value != it) {
                selectedTrack.value = it
            }
        }
    }

    sealed class State {
        object Playing: State()
        object Paused: State()
        object Initializing: State()
        object Uninitialized: State()
        object InitializationFailed: State()
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
        fun create(context: Context, music: MusicRepository): NowPlayingViewModel {
            return NowPlayingViewModel(context, music)
        }
    }

    fun playAlbum() {
        setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        music.onTrackSelected()
        play(music.tracks?.firstOrNull())
    }

    fun play(track: TrackEntity?) {
        music.onTrackSelected()
        track?.let { playInternal(it) }
    }

    fun shuffleAlbum() {
        setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        music.onTrackSelected()
        play(music.tracks?.randomOrNull())
    }

    fun setShuffleMode(mode: Int) = mediaBrowserConnection?.mediaController?.transportControls?.setShuffleMode(mode)

    private fun playInternal(track: TrackEntity) {
        playState.postValue(State.Initializing)
        selectedTrack.postValue(track)
        mediaBrowserConnection?.mediaController?.let { mc ->
            val extras = Bundle().apply {
                this.putString(MediaSessionManager.EXTRA_QUEUE_IDENTIFIER, track?.toMetadata()?.mediaId)
            }
            mc.sendCommand(MediaSessionManager.COMMAND_SWAP_QUEUE, extras, object : ResultReceiver(null) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    if (resultCode == MediaSessionManager.RESULT_ADD_QUEUE_ITEMS) {
                        mc.queue.clear()
                        music.tracks?.forEach { track ->
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
            inTime(4000) { playState.value = State.InitializationFailed }
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
