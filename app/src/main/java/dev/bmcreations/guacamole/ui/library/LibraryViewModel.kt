package dev.bmcreations.guacamole.ui.library

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.bmcreations.guacamole.ui.library.artists.Artist
import dev.bmcreations.networking.NetworkState
import dev.bmcreations.networking.Outcome
import dev.bmcreations.networking.api.apple.sources.LibrarySource
import dev.bmcreations.networking.api.apple.sources.RecentlyAddedDataFactory
import dev.bmcreations.guacamole.media.MusicQueue
import dev.bmcreations.guacamole.models.apple.*
import dev.bmcreations.networking.api.genius.sources.GeniusSearchSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.Executors


class LibraryViewModel(
    val librarySource: LibrarySource,
    val geniusSearch: GeniusSearchSource
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    var recentsNetworkState: LiveData<NetworkState>? = null
    var recentlyAdded: LiveData<PagedList<RecentlyAddedEntity>>? = null

    private var _playlists: List<LibraryPlaylist> = listOf()
        set(value) {
            field = value
            playlists.value = value
        }
    val playlists = MutableLiveData<List<LibraryPlaylist>>()

    private var tracks = MutableLiveData<List<Track>>()

    val songs = Transformations.map(tracks) { entities ->
        entities.map { entity ->
            TrackEntity(
                entity,
                entity.librarySongContainer(entities)
            )
        }
    }

    val artists = Transformations.map(tracks) { list ->
        list.asSequence()
            .mapNotNull { it.relationships?.artists?.data?.firstOrNull() }
            .map { Artist(it.attributes?.name) }
            .filterNot { it.name?.trim().isNullOrBlank() }
            .distinctBy { it.name }
            .sortedBy { it.name?.toLowerCase() }
            .toList()
    }

//    val albums = Transformations.map(tracks) { list ->
//        list.asSequence()
//            .mapNotNull { Album
//            song.relationships?.albums?.data?.firstOrNull()
//        }.map {  }
//    }

    init {
        initializeLibrarySongs()
        initializePlaylists()
        initializeRecents()
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

    private fun initializeLibrarySongs() {
        librarySource.getAllLibrarySongs(tracks)
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

    fun getLibraryAlbumById(id: String, cb: (Album) -> Unit) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryAlbumById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            cb.invoke(Album(outcome.data))
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

    fun getLibraryPlaylistById(id: String, cb: (Playlist) -> Unit) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryPlaylistById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            cb.invoke(Playlist(outcome.data))
                        }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }

    fun getLibraryPlaylistWithTracksById(id: String, cb: (Playlist) -> Unit) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryPlaylistWithTracksById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            cb.invoke(Playlist(outcome.data))
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
