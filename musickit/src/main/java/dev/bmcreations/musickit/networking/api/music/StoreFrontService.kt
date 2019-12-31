package dev.bmcreations.musickit.networking.api.music

import dev.bmcreations.guacamole.models.UserStoreFrontResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface StoreFrontService {

    @GET("me/storefront")
    fun getUserStoreFrontAsync(): Deferred<dev.bmcreations.guacamole.models.UserStoreFrontResult>
}
