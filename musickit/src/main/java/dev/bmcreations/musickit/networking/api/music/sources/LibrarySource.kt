package dev.bmcreations.musickit.networking.api.music.sources

import dev.bmcreations.musickit.extensions.paged
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.provideLibraryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class LibrarySource(
    private val retrofit: Retrofit,
    private val storeFrontSource: StoreFrontSource
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val library by lazy {
        provideLibraryService(retrofit)
    }

    suspend fun getUserRecentlyAdded(limit: Int? = null, offset: Int? = null): Outcome<RecentlyAddedResult> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<RecentlyAddedResult>
        val req = library.getUserRecentlyAddedAsync(limit, offset)
        try {
            req.await().run {
                ret = Outcome.success(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

    suspend fun getLibraryAlbumById(id: String): Outcome<LibraryAlbum?> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<LibraryAlbum?>
        val req = library.getLibraryAlbumByIdAsync(id)
        try {
            req.await().run {
                val album = this.data?.first()
                album?.apply {
                    name = attributes?.name
                    artist = attributes?.artistName
                    trackList = relationships?.tracks?.data?.filterNotNull()
                }
                ret = Outcome.success(album)

            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

    suspend fun getAllLibraryAlbums(): Outcome<List<LibraryAlbum>> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<List<LibraryAlbum>>
        try {
            val results = paged<List<LibraryAlbum>, LibraryAlbumResult>(
                pagedCall = { next -> library.getAllLibraryAlbumsAsync(offset = next?.substringAfter("offset=")?.toInt()) },
                scope = this
            )
            ret = Outcome.success(results.apply {
                this?.map {
                    it.name = it.attributes?.name
                    it.artist = it.attributes?.artistName
                    it.trackList = it.relationships?.tracks?.data?.filterNotNull()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

    suspend fun getAllLibraryPlaylists(): Outcome<List<LibraryPlaylist>> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<List<LibraryPlaylist>>
        val req = library.getAllLibraryPlaylistsAsync()
        try {
            req.await().run { ret = Outcome.success(this.data) }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

    suspend fun getLibraryPlaylistById(id: String): Outcome<LibraryPlaylist> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<LibraryPlaylist>
        val req = library.getLibraryPlaylistByIdAsync(id)
        try {
            req.await().run {
                val record = this.data.first()
                ret = Outcome.success(record.apply {
                    name = record.attributes?.name
                    artist = "Various Artists"
                    isPlaylist = true
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

    suspend fun getLibraryPlaylistWithTracksById(id: String): Outcome<LibraryPlaylist> {
        storeFrontSource.updateStoreIfNeeded()
        var ret: Outcome<LibraryPlaylist>
        val playlistReq = library.getLibraryPlaylistByIdAsync(id)
        try {
            playlistReq.await().run {
                val record = this.data.first()
                val tracksReq = library.getLibraryPlaylistTracksByIdAsync(id)
                tracksReq.await().run {
                    record.apply {
                        name = record.attributes?.name
                        artist = "Various Artists"
                        trackList = data
                        isPlaylist = true
                    }
                    ret = Outcome.success(record)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }

}
