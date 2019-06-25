package dev.bmcreations.musickit.networking.api.music.repository

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.apple.android.sdk.authentication.TokenProvider
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.extensions.*
import dev.bmcreations.musickit.networking.provideLibraryService
import dev.bmcreations.musickit.networking.provideRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger

class MusicRepository private constructor() : AnkoLogger {

    var devToken: String = ""
    var userToken: String? = null

    companion object {
        const val API_VERSION = 1
        const val BASE_URL = "https://api.music.apple.com"

        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(provider: TokenProvider): MusicRepository {
            val tmp = INSTANCE
            if (tmp != null) {
                tmp.userToken = provider.userToken
                return tmp
            }

            return synchronized(this) {
                val instance: MusicRepository = MusicRepository().apply {
                    this.devToken = provider.developerToken
                    this.userToken = provider.userToken
                }

                INSTANCE = instance
                instance
            }

        }
    }

    private val retrofit by lazy {
        provideRetrofit(baseUrl = "$BASE_URL/v$API_VERSION/")
    }

    private val library by lazy {
        provideLibraryService(retrofit)
    }

    var tracks: MutableList<TrackEntity>?  = mutableListOf()

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
                            tracks?.addAll(it)
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
                                uiScope.launch { this@MusicRepository.tracks?.addAll(ret.map { PlaylistTrackEntity(it) }) }
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

    fun getTrack(id: String): TrackEntity? {
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