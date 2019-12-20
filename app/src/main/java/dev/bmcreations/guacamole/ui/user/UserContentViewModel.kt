package dev.bmcreations.guacamole.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.Playlist
import dev.bmcreations.musickit.networking.api.models.RecentlyPlayedResource
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class UserContentViewModel private constructor(
    private val musicRepo: MusicRepository
): CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    private val _recentlyPlayed = MutableLiveData<List<RecentlyPlayedResource>>()
    val recentlyPlayed : LiveData<List<RecentlyPlayedResource>> get() = _recentlyPlayed

    fun getRecentlyPlayedHistory() {
        viewModelScope.launch {
            when (val outcome = musicRepo.getRecentlyPlayedItems(10)) {
                is Outcome.Success -> {
                    viewModelScope.launch(Dispatchers.Main) { _recentlyPlayed.value = outcome.data }
                }
                is Outcome.Failure -> {
                    info { outcome.e.localizedMessage }
                }
            }
        }
    }

    companion object {
        fun create(musicRepo: MusicRepository): UserContentViewModel {
            return UserContentViewModel(musicRepo)
        }
    }
}
