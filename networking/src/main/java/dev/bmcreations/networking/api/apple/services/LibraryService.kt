package dev.bmcreations.networking.api.apple.services

import dev.bmcreations.guacamole.models.LibraryPlaylistResult
import dev.bmcreations.guacamole.models.RecentlyAddedResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LibraryService {
    @GET("me/library/recently-added")
    fun getUserRecentlyAddedAsync(@Query("limit") limit: Int?, @Query("offset") offset: Int?): Deferred<RecentlyAddedResult>

    @GET("me/library/albums")
    fun getAllLibraryAlbumsAsync(
        @Query("include") relationships: String? = "tracks",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int?): Deferred<dev.bmcreations.guacamole.models.LibraryAlbumResult>

    @GET("me/library/albums/{id}")
    fun getLibraryAlbumByIdAsync(@Path("id") id: String): Deferred<dev.bmcreations.guacamole.models.LibraryAlbumResult>

    @GET("me/library/artists/{id}")
    fun getLibraryArtistByIdAsync(@Path("id") id: String): Deferred<dev.bmcreations.guacamole.models.LibraryArtistsResult>

    @GET("me/library/playlists/{id}")
    fun getLibraryPlaylistByIdAsync(@Path("id") id: String): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists")
    fun getAllLibraryPlaylistsAsync(): Deferred<LibraryPlaylistResult>

    @GET("me/library/playlists/{id}/tracks")
    fun getLibraryPlaylistTracksByIdAsync(@Path("id") id: String?): Deferred<dev.bmcreations.guacamole.models.TrackResult>

    @GET("me/library/songs")
    fun getAllLibrarySongsAsync(
        @Query("include") relationships: String? = "albums,artists",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int?
    ): Deferred<dev.bmcreations.guacamole.models.LibrarySongResult>
}
