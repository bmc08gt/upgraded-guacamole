package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.viewmodel.SingleLiveEvent
import dev.bmcreations.musickit.networking.api.models.TrackEntity

class NowPlayingViewModel private constructor(context: Context): ViewModel() {

    val selectedTrack:  MutableLiveData<TrackEntity?> = MutableLiveData()
    val playState: MutableLiveData<State> = SingleLiveEvent()

    sealed class State {
        object Playing: State()
        object Paused: State()
        object Initializing: State()
        object Uninitialized: State()
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
}