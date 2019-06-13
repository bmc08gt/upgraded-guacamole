package dev.bmcreations.musickit.networking.api.music.repository

import android.content.Context
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.LibraryAlbum
import dev.bmcreations.musickit.networking.api.models.Playlist
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity
import dev.bmcreations.musickit.networking.provideLibraryService
import dev.bmcreations.musickit.networking.provideRetrofit
import org.jetbrains.anko.AnkoLogger

class MusicRepository(val context: Context,
                      val devToken: String,
                      val userToken: String?): AnkoLogger {

    companion object {
        const val API_VERSION = 1
        const val BASE_URL = "https://api.music.apple.com"
    }

    private val retrofit by lazy {
        provideRetrofit(baseUrl = "$BASE_URL/v$API_VERSION/")
    }

    private val library by lazy {
        provideLibraryService(retrofit)
    }

    suspend fun getUserPlaylists(): Outcome<List<Playlist>> {
        var ret: Outcome<List<Playlist>> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val req = library.getUserPlaylistsAsync(bearer, token)
            try {
                req.await().run {
                    ret = Outcome.success(this.data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }

    suspend fun getUserRecentlyAdded(): Outcome<List<RecentlyAddedEntity>> {
        var ret: Outcome<List<RecentlyAddedEntity>> = Outcome.failure(Throwable("Auth token is null"))
        userToken?.let { token ->
            val bearer = "Bearer $devToken"
            val req = library.getUserRecentlyAddedAsync(bearer, token)
            try {
                req.await().run {
                    ret = Outcome.success(this.data)
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
                    ret = Outcome.success(this.data.first())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ret = Outcome.failure(e)
            }
        }
        return ret
    }
}