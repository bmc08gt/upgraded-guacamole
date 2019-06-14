package dev.bmcreations.guacamole.ui.library

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.extensions.uiScope
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class LibraryViewModel(context: Context): ViewModel(), AnkoLogger {



    private val musicRepo by lazy {
        MusicRepository(context, TokenProvider.with(context).devToken, TokenProvider.with(context).userToken)
    }

    val recentlyAdded: MutableLiveData<List<RecentlyAddedEntity>> = MutableLiveData()

    val selected: MutableLiveData<LibraryResult?> = MutableLiveData()

    init {
        recentlyAdded.value = emptyList()
        selected.value = null
    }

    fun refresh() {
        updateRecentlyAdded()
    }

    fun updateRecentlyAdded() {
        musicRepo.let { repo ->
            uiScope.launch(Dispatchers.IO) {
                val outcome = repo.getUserRecentlyAdded()
                when (outcome) {
                    is Outcome.Success -> {
                        info { "recently added items: ${outcome.data.size}" }
                        uiScope.launch { recentlyAdded.value = outcome.data }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun getLibraryAlbumById(id: String) {
        musicRepo.let { repo ->
            uiScope.launch(Dispatchers.IO) {
                val outcome = repo.getLibraryAlbumById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        uiScope.launch { selected.value = Album(outcome.data) }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun getLibraryPlaylistById(id: String) {
        musicRepo.let { repo ->
            uiScope.launch(Dispatchers.IO) {
                val outcome = repo.getLibraryPlaylistById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        uiScope.launch { selected.value = Playlist(outcome.data) }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun getLibraryPlaylistWithTracksById(id: String) {
        musicRepo.let { repo ->
            uiScope.launch(Dispatchers.IO) {
                val outcome = repo.getLibraryPlaylistWithTracksById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        uiScope.launch { selected.value = Playlist(outcome.data) }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }
}