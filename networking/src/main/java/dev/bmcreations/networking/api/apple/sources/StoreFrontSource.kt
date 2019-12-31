package dev.bmcreations.networking.api.apple.sources

import dev.bmcreations.networking.provideStoreFrontService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class StoreFrontSource(
    private val retrofit: Retrofit
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private var _store: dev.bmcreations.guacamole.models.UserStoreFront? = null
    val store get() = _store

    private val storeFront by lazy {
        provideStoreFrontService(retrofit)
    }

    init {
        updateStoreIfNeeded()
    }

    fun updateStoreIfNeeded() {
        if (store == null) {
            launch {
                val req = storeFront.getUserStoreFrontAsync()
                try {
                    req.await().run {
                        _store = this.data.first()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
