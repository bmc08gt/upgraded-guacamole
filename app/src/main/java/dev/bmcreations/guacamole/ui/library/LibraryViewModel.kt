package dev.bmcreations.guacamole.ui.library

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.bmcreations.guacamole.ui.library.artists.Artist
import dev.bmcreations.musickit.networking.NetworkState
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.api.music.sources.LibrarySource
import dev.bmcreations.musickit.networking.api.music.sources.RecentlyAddedDataFactory
import dev.bmcreations.musickit.queue.MusicQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.Executors


class LibraryViewModel(
    val librarySource: LibrarySource,
    val musicQueue: MusicQueue
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    var recentsNetworkState: LiveData<NetworkState>? = null
    var recentlyAdded: LiveData<PagedList<RecentlyAddedEntity>>? = null

    private var _playlists: List<LibraryPlaylist> = listOf()
        set(value) {
            field = value
            playlists.value = value
        }
    val playlists = MutableLiveData<List<LibraryPlaylist>>()

    private var _albums: List<LibraryAlbum> = listOf()
        set(value) {
            field = value
            albums.value = value
        }
    val albums = MutableLiveData<List<LibraryAlbum>>()

    val artists = Transformations.map(albums) { list ->
        list.map { Artist(name = it.artist) }.sortedBy { it.name?.toLowerCase() }.distinctBy { it.name }.filterNot { it.name?.trim().isNullOrBlank() }
    }

    val songs = Transformations.map(albums) { albums -> albums.map { it.trackList }}

    val selected: MutableLiveData<LibraryResult?> = MutableLiveData()

    init {
        initializeLibraryAlbums()
        initializePlaylists()
        initializeRecents()

        selected.value = null
    }

    private fun initializePlaylists() {
        viewModelScope.launch {
            librarySource.getAllLibraryPlaylists().let {
                when (it) {
                    is Outcome.Success -> viewModelScope.launch(Dispatchers.Main) {
                        _playlists = it.data ?: emptyList()
                    }
                    is Outcome.Failure -> it.e.printStackTrace()
                    else -> {
                    }
                }
            }
        }
    }

    private fun initializeLibraryAlbums() {
        viewModelScope.launch {
            librarySource.getAllLibraryAlbums().let {
                when (it) {
                    is Outcome.Success -> viewModelScope.launch(Dispatchers.Main) {
                        _albums = it.data ?: emptyList()
                    }
                    is Outcome.Failure -> it.e.printStackTrace()
                    else -> {
                    }
                }
            }
        }
    }

    private fun initializeRecents() {
        val recentFactory = RecentlyAddedDataFactory().apply {
            this.provideLibrarySource(librarySource)
        }
        recentsNetworkState =
            Transformations.switchMap(recentFactory.mutableLiveData) { source -> source.networkState }
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .setPageSize(10).build()

        recentlyAdded = LivePagedListBuilder(recentFactory, pagedListConfig)
            .setFetchExecutor(Executors.newFixedThreadPool(5))
            .build()
    }

    fun getLibraryAlbumById(id: String) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryAlbumById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            selected.value = Album(outcome.data)
                        }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun refreshLibraryPlaylists() {
        initializePlaylists()
    }

    fun getLibraryPlaylistById(id: String) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryPlaylistById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            selected.value = Playlist(outcome.data)
                        }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun getLibraryPlaylistWithTracksById(id: String) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryPlaylistWithTracksById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            selected.value = Playlist(outcome.data)
                        }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }
}
