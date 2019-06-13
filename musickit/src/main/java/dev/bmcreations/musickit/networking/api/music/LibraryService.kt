package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.musickit.networking.api.models.PlaylistResult
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedResult
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header

interface LibraryService {
    @GET("me/library/playlists")
    fun getUserPlaylistsAsync(@Header("Authorization") devToken: String,
                              @Header("Music-User-Token") token: String): Deferred<PlaylistResult>

    @GET("me/library/recently-added")
    fun getUserRecentlyAddedAsync(@Header("Authorization") devToken: String,
                              @Header("Music-User-Token") token: String): Deferred<RecentlyAddedResult>
}