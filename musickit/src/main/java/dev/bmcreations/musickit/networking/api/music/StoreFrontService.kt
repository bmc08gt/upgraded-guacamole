package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.musickit.networking.api.models.UserStoreFrontResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header

interface StoreFrontService {

    @GET("me/storefront")
    fun getUserStoreFrontAsync(@Header("Authorization") devToken: String,
                               @Header("Music-User-Token") token: String): Deferred<UserStoreFrontResult>
}