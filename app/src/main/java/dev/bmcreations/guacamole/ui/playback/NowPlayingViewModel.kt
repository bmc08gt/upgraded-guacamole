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
import kotlinx.coroutines.Job
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class NowPlayingViewModel private constructor(val context: Context): ViewModel(), AnkoLogger {

    val selectedTrack = MutableLiveData<TrackEntity?>()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    var initializationFailedJob : Job? = null

    val music by lazy {
        MusicRepository.getInstance(TokenProvider.with(context))
    }

    private val mediaBrowserConnection: MediaBrowserConnection? by lazy {
        MediaBrowserConnection(context,
            { data, i ->
                info { "conn:: data=$data, playbackState=$i" }
            },
            { items ->
                info { "conn:: items=$items" }
            })
    }
    private val mediaBrowserCallback by lazy {
        MediaBrowserCallback(mediaBrowserConnection) { data, i ->
            info { "cb:: $data, $i" }
            when (i) {
                PlaybackStateCompat.STATE_BUFFERING -> onTrackBuffering()
                PlaybackStateCompat.STATE_PLAYING -> onMusicStarted()
                PlaybackStateCompat.STATE_PAUSED -> onMusicPaused()
                PlaybackStateCompat.STATE_STOPPED -> onMusicStopped()
            }

            updateCurrentTrackFromCallback(data?.get(MediaSessionManager.EXTRA_CURRENT_TRACK) as? TrackEntity)
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
        fun create(context: Context): NowPlayingViewModel {
            return NowPlayingViewModel(context)
        }
    }

    fun updatePlayingTrack(track: TrackEntity?) {
        play(track)
        selectedTrack.postValue(track)
    }

    private fun play(track: TrackEntity?) {
        playState.postValue(State.Initializing)
        mediaBrowserConnection?.mediaController?.let { mc ->
            val extras = Bundle().apply {
                this.putString(MediaSessionManager.EXTRA_QUEUE_IDENTIFIER, track?.toMetadata()?.mediaId)
            }
            mc.sendCommand(MediaSessionManager.COMMAND_SWAP_QUEUE, extras, object : ResultReceiver(null) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    if (resultCode == MediaSessionManager.RESULT_ADD_QUEUE_ITEMS) {
                        music.tracks?.forEach { track -> mc.addQueueItem(track.toMetadata().description) }
                        mc.transportControls?.prepare()
                    }

                    track?.let {
                        mc.transportControls?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
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