package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.musickit.networking.api.models.CatalogAlbum
import dev.bmcreations.musickit.networking.api.models.CatalogAlbumResult
import dev.bmcreations.musickit.networking.api.models.CatalogPlaylistResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CatalogService {
    @GET("catalog/{storefront}/playlists/{id}")
    fun getPlaylistByIdAsync(@Header("Authorization") devToken: String,
                             @Header("Music-User-Token") token: String,
                             @Path("storefront") lang: String,
                             @Path("id") id: String): Deferred<CatalogPlaylistResult>

    @GET("catalog/{storefront}/albums/{id}")
    fun getAlbumByIdAsync(@Header("Authorization") devToken: String,
                             @Header("Music-User-Token") token: String,
                             @Path("storefront") lang: String,
                             @Path("id") id: String): Deferred<CatalogAlbumResult>
}
