package dev.bmcreations.guacamole.ui.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bmcreations.guacamole.library.Library
import dev.bmcreations.guacamole.models.apple.*
import dev.bmcreations.guacamole.ui.library.artists.Artist
import dev.bmcreations.networking.Outcome
import dev.bmcreations.networking.api.apple.sources.LibrarySource
import dev.bmcreations.networking.api.genius.sources.GeniusSearchSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class LibraryViewModel(
    val library: Library,
    val librarySource: LibrarySource,
    val geniusSearch: GeniusSearchSource
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    var recentlyAdded = MutableLiveData<List<RecentlyAddedEntity>>()

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
        launch {
            // get cached recents
            val cached = library.songs()
            info { "initial songs cache=${cached.count()}" }
            launch(Dispatchers.Main) { tracks.value = cached  }

            librarySource.getAllLibrarySongs(this) { scope, page ->
                viewModelScope.launch {
                    library.addSongs(page)

                    val cached = library.songs()
                    info { "updated songs cache=${cached.count()}" }
                    launch(Dispatchers.Main) { tracks.value = cached  }
                }
            }
        }
    }

    private fun initializeRecents() {
        launch {
            // get cached recents
            val cached = library.recentlyAdded()
            info { "initial recents cache=${cached.count()}" }
            launch(Dispatchers.Main) { recentlyAdded.value = cached  }

//            // trigger network request for more
            librarySource.getUserRecentlyAdded(this) { scope, items ->
                scope.launch {
                    library.addRecentlyAddedItems(items)
                    val cached = withContext(scope.coroutineContext) { library.recentlyAdded() }
                    info { "updated recents cache=${cached.count()}" }
                    launch(Dispatchers.Main) { recentlyAdded.value = cached  }
                }
            }
        }
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
