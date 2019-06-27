package dev.bmcreations.musickit.networking.api.music.repository

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.MediaBrowserServiceCompat
import com.apple.android.sdk.authentication.TokenProvider
import dev.bmcreations.musickit.networking.*
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger

class MusicRepository private constructor() : AnkoLogger {

    var devToken: String = ""
    var userToken: String? = null

    var userStore: UserStoreFront? = null

    companion object {
        const val API_VERSION = 1
        const val BASE_URL = "https://api.music.apple.com"

        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(provider: TokenProvider): MusicRepository {
            val tmp = INSTANCE
            if (tmp != null) {
                tmp.userToken = provider.userToken
                if (tmp.userStore == null) {
                    tmp.updateUserStoreFront()
                }
                return tmp
            }

            return synchronized(this) {
                val instance: MusicRepository = MusicRepository().apply {
                    this.devToken = provider.developerToken
                    this.userToken = provider.userToken
                    updateUserStoreFront()
                }

                INSTANCE = instance
                instance
            }

        }
    }

    private val retrofit by lazy {
        provideRetrofit(baseUrl = "$BASE_URL/v$API_VERSION/")
    }

    private val storeFront by lazy {
        provideStoreFrontService(retrofit)
    }

    private val library by lazy {
        provideLibraryService(retrofit)
    }

    private val catalog by lazy {
        provideCatalogService(retrofit)
    }

    var _tracks: MutableList<TrackEntity>? = mutableListOf()
    var tracks: MutableList<TrackEntity>? = mutableListOf()

    fun onTrackSelected() {
        tracks = _tracks
        _tracks = null
    }

    private fun updateUserStoreFront() {
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            uiScope.launch(Dispatchers.IO) {
                val req = storeFront.getUserStoreFrontAsync(bearer, token)
                try {
                    req.await().run {
                        userStore = this.data.first()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getUserRecentlyAdded(limit: Int? = null, offset: Int? = null): Outcome<RecentlyAddedResult> {
        var ret: Outcome<RecentlyAddedResult> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val req = library.getUserRecentlyAddedAsync(bearer, token, limit, offset)
            try {
                req.await().run {
                    ret = Outcome.success(this)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }

    suspend fun getLibraryAlbumById(id: String): Outcome<LibraryAlbum> {
        var ret: Outcome<LibraryAlbum> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val req = library.getLibraryAlbumByIdAsync(bearer, token, id)
            try {
                req.await().run {
                    val album = this.data.first()
                    ret = Outcome.success(album)
                    uiScope.launch {
                        this@run.data.first().relationships?.tracks?.data?.map { AlbumTrackEntity(it!!, album) }?.let {
                            _tracks = it.toMutableList()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }

    suspend fun getAllLibraryPlaylists(): Outcome<List<LibraryPlaylist>> {
        var ret: Outcome<List<LibraryPlaylist>> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            userStore?.id?.let { store ->
                val req = library.getAllLibraryPlaylistsAsync(bearer, token)
                try {
                    req.await().run {
                        val res = this.data
                        uiScope.launch(Dispatchers.Unconfined) {
                            res.forEach {
                                val playlist = it
                                playlist.attributes?.playParams?.globalId?.let { id ->
                                    val tracksReq = catalog.getPlaylistByIdAsync(bearer, token, store, id)
                                    tracksReq.await().run {
                                        val catalogEntry = this.data.first()
                                        playlist.apply {
                                            this.attributes?.curator = catalogEntry.attributes?.curatorName
                                        }
                                    }
                                }
                            }
                        }
                        ret = Outcome.success(this.data)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ret = Outcome.failure(e)
                }
            } ?: run {
                ret = Outcome.failure(Throwable("storefront is null"))
            }
        }
        return ret
    }

    suspend fun getLibraryPlaylistById(id: String): Outcome<LibraryPlaylist> {
        var ret: Outcome<LibraryPlaylist> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val req = library.getLibraryPlaylistByIdAsync(bearer, token, id)
            try {
                req.await().run {
                    ret = Outcome.success(this.data.first())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }

    suspend fun getLibraryPlaylistWithTracksById(id: String): Outcome<LibraryPlaylist> {
        var ret: Outcome<LibraryPlaylist> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val playlistReq = library.getLibraryPlaylistByIdAsync(bearer, token, id)
            try {
                playlistReq.await().run {
                    val playlist = this.data.first()
                    val tracksReq = library.getLibraryPlaylistTracksByIdAsync(bearer, token, id)
                    tracksReq.await().run {
                        val tracks = this.data
                        ret = Outcome.success(playlist.apply {
                            this.tracks = tracks.also { ret ->
                                uiScope.launch {
                                    this@MusicRepository._tracks = ret.map { PlaylistTrackEntity(it) }.toMutableList() }
                            }
                        })
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }

    fun getTrackByMetadataMediaId(id: String): TrackEntity? {
        return tracks?.find {
            when (it) {
                is PlaylistTrackEntity -> it.toMetadata().mediaId == id
                is AlbumTrackEntity -> it.toMetadata().mediaId == id
            }
        }
    }

    fun getTrackByMediaId(id: String): TrackEntity? {
        return tracks?.find {
            when (it) {
                is PlaylistTrackEntity -> it.track.id == id
                is AlbumTrackEntity -> it.track.id == id
            }
        }
    }

    fun getTrackByCatalogId(id: String): TrackEntity? {
        return tracks?.find {
            when (it) {
                is PlaylistTrackEntity -> it.track.attributes?.playParams?.catalogId == id
                is AlbumTrackEntity -> it.track.attributes?.playParams?.catalogId == id
            }
        }
    }

    fun loadMediaItems(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        result.sendResult(tracks?.map {
            val metadata = it.toMetadata()
            val item = MediaDescriptionCompat.Builder()
                .setMediaId(metadata.mediaId)
                .setTitle(metadata.songName)
                .setSubtitle(metadata.artistName)
                .setIconUri(Uri.parse(metadata.albumArtworkUrl))
                .setExtras(metadata.bundle)
                .setMediaUri(Uri.parse(metadata.fullArtworkUri)).build()

            MediaBrowserCompat.MediaItem(item, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE) }?.toMutableList())
    }
}