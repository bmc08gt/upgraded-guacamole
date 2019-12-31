package dev.bmcreations.networking.api.genius.sources

import dev.bmcreations.guacamole.models.genius.GeniusSearchHit
import dev.bmcreations.networking.Outcome
import dev.bmcreations.networking.api.provideGeniusSearchService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

class GeniusSearchSource(
    private val retrofit: Retrofit
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val search by lazy {
        provideGeniusSearchService(retrofit)
    }

    suspend fun searchBy(query: String?, take: Int = 1): Outcome<List<GeniusSearchHit>> {
        var ret: Outcome<List<GeniusSearchHit>>
        val req = search.searchAsync(query)
        try {
            req.await().run {
                ret = if (this.meta.status in 200..299) {
                    Outcome.success(this.response.hits.take(take))
                } else {
                    Outcome.failure(Throwable("Error hit: code=${this.meta.status}"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ret = Outcome.failure(e)
        }
        return ret
    }
}
