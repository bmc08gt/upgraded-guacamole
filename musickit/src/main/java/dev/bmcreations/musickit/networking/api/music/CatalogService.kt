package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.guacamole.models.CatalogAlbum
import dev.bmcreations.guacamole.models.CatalogAlbumResult
import dev.bmcreations.guacamole.models.CatalogPlaylistResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogService {
    @GET("catalog/{storefront}/playlists/{id}")
    fun getPlaylistByIdAsync(
        @Path("storefront") lang: String,
        @Path("id") id: String
    ): Deferred<dev.bmcreations.guacamole.models.CatalogPlaylistResult>

    @GET("catalog/{storefront}/albums/{id}")
    fun getAlbumByIdAsync(
        @Path("storefront") lang: String,
        @Path("id") id: String
    ): Deferred<dev.bmcreations.guacamole.models.CatalogAlbumResult>
}
