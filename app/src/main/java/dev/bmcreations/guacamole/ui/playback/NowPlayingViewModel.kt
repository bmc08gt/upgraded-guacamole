package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.ui.details.TrackEntity

class NowPlayingViewModel private constructor(context: Context): ViewModel() {

    val selectedTrack:  MutableLiveData<TrackEntity?> = MutableLiveData()

    init {
        selectedTrack.value = null
    }

    companion object Factory {
        fun create(context: Context): NowPlayingViewModel {
            return NowPlayingViewModel(context)
        }
    }

    fun updatePlayingTrack(track: TrackEntity?) {
        selectedTrack.postValue(track)
    }
}