package dev.bmcreations.networking.api.apple.services

import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface StoreFrontService {

    @GET("me/storefront")
    fun getUserStoreFrontAsync(): Deferred<dev.bmcreations.guacamole.models.UserStoreFrontResult>
}
