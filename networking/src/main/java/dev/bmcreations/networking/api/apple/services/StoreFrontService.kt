package dev.bmcreations.networking.api.apple.services

import dev.bmcreations.guacamole.models.apple.UserStoreFrontResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface StoreFrontService {

    @GET("me/storefront")
    fun getUserStoreFrontAsync(): Deferred<UserStoreFrontResult>
}
