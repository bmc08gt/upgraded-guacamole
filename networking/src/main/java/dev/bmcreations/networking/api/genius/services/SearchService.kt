package dev.bmcreations.networking.api.genius.services

import dev.bmcreations.guacamole.models.genius.GeniusSearchResult
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/search")
    fun searchAsync(@Query("q") query: String?): Deferred<GeniusSearchResult>
}
