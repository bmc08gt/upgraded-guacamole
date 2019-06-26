package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.musickit.networking.api.models.*
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface LibraryService {
    @GET("me/library/recently-added")
    fun getUserRecentlyAddedAsync(@Header("Authorization") devToken: String,
                              @Header("Music-User-Token") token: String, @Query("limit") limit: Int?, @Query("offset") offset: Int?): Deferred<RecentlyAddedResult>

    @GET("me/library/albums/{id}")
    fun getLibraryAlbumByIdAsync(@Header("Authorization") devToken: String,
                                 @Header("Music-User-Token") token: String, @Path("id") id: String): Deferred<LibraryAlbumResult>

    @GET("me/library/playlists/{id}")
    fun getLibraryPlaylistByIdAsync(@Header("Authorization") devToken: String,
                                    @Header("Music-User-Token") token: String, @Path("id") id: String): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists/")
    fun getAllLibraryPlaylistsAsync(@Header("Authorization") devToken: String,
                                    @Header("Music-User-Token") token: String): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists/{id}/tracks")
    fun getLibraryPlaylistTracksByIdAsync(@Header("Authorization") devToken: String,
                                    @Header("Music-User-Token") token: String, @Path("id") id: String?): Deferred<PlaylistTrackResult>
}