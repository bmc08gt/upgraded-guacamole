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
    fun getUserRecentlyAddedAsync(@Query("limit") limit: Int?, @Query("offset") offset: Int?): Deferred<RecentlyAddedResult>

    @GET("me/library/albums/{id}")
    fun getLibraryAlbumByIdAsync(@Path("id") id: String): Deferred<LibraryAlbumResult>

    @GET("me/library/playlists/{id}")
    fun getLibraryPlaylistByIdAsync(@Path("id") id: String): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists/")
    fun getAllLibraryPlaylistsAsync(): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists/{id}/tracks")
    fun getLibraryPlaylistTracksByIdAsync(@Path("id") id: String?): Deferred<TrackResult>
}
