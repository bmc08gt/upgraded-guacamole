package dev.bmcreations.guacamole.ui.library

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.bmcreations.guacamole.models.LibraryPlaylist
import dev.bmcreations.guacamole.models.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.librarySongContainer
import dev.bmcreations.guacamole.ui.library.artists.Artist
import dev.bmcreations.musickit.networking.NetworkState
import dev.bmcreations.musickit.networking.Outcome
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

    private var tracks = MutableLiveData<List<dev.bmcreations.guacamole.models.Track>>()

    val songs = Transformations.map(tracks) { entities ->
        entities.map { entity ->
            dev.bmcreations.guacamole.models.TrackEntity(
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

    val selected: MutableLiveData<dev.bmcreations.guacamole.models.LibraryResult?> = MutableLiveData()

    init {
        initializeLibrarySongs()
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

    fun getLibraryAlbumById(id: String) {
        librarySource.let { repo ->
            viewModelScope.launch {
                val outcome = repo.getLibraryAlbumById(id)
                when (outcome) {
                    is Outcome.Success -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            selected.value =
                                dev.bmcreations.guacamole.models.Album(outcome.data)
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
                            selected.value =
                                dev.bmcreations.guacamole.models.Playlist(outcome.data)
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
                            selected.value =
                                dev.bmcreations.guacamole.models.Playlist(outcome.data)
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
