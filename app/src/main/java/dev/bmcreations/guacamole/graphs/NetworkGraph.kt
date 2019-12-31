package dev.bmcreations.guacamole.graphs

import android.content.Context
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.networking.api.apple.AppleMusicApi
import dev.bmcreations.networking.api.apple.sources.LibrarySource
import dev.bmcreations.networking.api.apple.sources.StoreFrontSource
import dev.bmcreations.networking.api.provideRetrofit

interface NetworkGraph {
    val librarySource: LibrarySource
    val storeFrontSource: StoreFrontSource
}

class NetworkGraphImpl(
    appContext: Context,
    tokenProvider: TokenProvider
) : NetworkGraph {

    private val retrofit by lazy {
        provideRetrofit(
            baseUrl = "${AppleMusicApi.BASE_URL}/v${AppleMusicApi.VERSION}/",
            tokenProvider = tokenProvider
        )
    }

    override val storeFrontSource: StoreFrontSource = StoreFrontSource(retrofit)
    override val librarySource: LibrarySource = LibrarySource(retrofit, storeFrontSource)
}
