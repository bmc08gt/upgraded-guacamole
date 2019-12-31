package dev.bmcreations.networking.api.apple.services

import dev.bmcreations.guacamole.models.apple.CatalogAlbumResult
import dev.bmcreations.guacamole.models.apple.CatalogPlaylistResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogService {
    @GET("catalog/{storefront}/playlists/{id}")
    fun getPlaylistByIdAsync(
        @Path("storefront") lang: String,
        @Path("id") id: String
    ): Deferred<CatalogPlaylistResult>

    @GET("catalog/{storefront}/albums/{id}")
    fun getAlbumByIdAsync(
        @Path("storefront") lang: String,
        @Path("id") id: String
    ): Deferred<CatalogAlbumResult>
}
