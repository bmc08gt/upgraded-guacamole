package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bmcreations.guacamole.extensions.inTime
import dev.bmcreations.guacamole.viewmodel.SingleLiveEvent
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NowPlayingViewModel private constructor(context: Context): ViewModel() {

    val selectedTrack:  MutableLiveData<TrackEntity?> = MutableLiveData()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    var initializationFailedJob : Job? = null

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
    }

    companion object Factory {
        fun create(context: Context): NowPlayingViewModel {
            return NowPlayingViewModel(context)
        }
    }

    fun updatePlayingTrack(track: TrackEntity?) {
        playState.postValue(State.Initializing)
        waitForMusicPlayback()
        selectedTrack.postValue(track)
    }

    fun playPause() {
        playState.value?.let {
            when (it) {
                is State.Playing -> {
                    playState.value = State.Paused
                    // TODO: Fire off pause
                }
                is State.Paused -> {
                    playState.value = State.Playing
                    // TODO: Fire off play
                }
                is State.Uninitialized -> {
                    playState.value = State.Initializing
                    // TODO: Fire off play
                }
            }
        }
    }

    private fun waitForMusicPlayback() {
        initializationFailedJob = inTime(4000) { playState.value = State.InitializationFailed }
        initializationFailedJob?.start()
    }

    fun onMusicStarted() {
        // TODO: actually wire up to mediaplayer from musickit
        initializationFailedJob?.cancel()
    }
}