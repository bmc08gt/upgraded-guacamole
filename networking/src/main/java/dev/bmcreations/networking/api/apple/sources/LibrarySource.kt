package dev.bmcreations.networking.api.apple.sources

import androidx.lifecycle.MutableLiveData
import dev.bmcreations.guacamole.models.LibraryAlbum
import dev.bmcreations.guacamole.models.LibraryPlaylist
import dev.bmcreations.guacamole.models.RecentlyAddedResult
import dev.bmcreations.guacamole.models.urlWithDimensions
import dev.bmcreations.guacamole.operator.paged
import dev.bmcreations.networking.Outcome
import dev.bmcreations.networking.provideLibraryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                    artwork = attributes?.artwork?.urlWithDimensions
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
                    artwork = this.attributes?.artwork?.urlWithDimensions
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
                        artwork = this.attributes?.artwork?.urlWithDimensions
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

    fun getAllLibrarySongs(tracks: MutableLiveData<List<dev.bmcreations.guacamole.models.Track>>) {
        storeFrontSource.updateStoreIfNeeded()
        try {
            paged(
                pagedCall = { next ->
                    library.getAllLibrarySongsAsync(
                        offset = next?.substringAfter(
                            "offset="
                        )?.toInt()
                    )
                },
                scope = this,
                liveData = tracks
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
