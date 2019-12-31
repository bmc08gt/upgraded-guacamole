package dev.bmcreations.guacamole.graphs

import android.content.Context
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.networking.BuildConfig
import dev.bmcreations.networking.api.apple.AppleMusicApi
import dev.bmcreations.networking.api.apple.sources.LibrarySource
import dev.bmcreations.networking.api.apple.sources.StoreFrontSource
import dev.bmcreations.networking.api.genius.GeniusApi
import dev.bmcreations.networking.api.genius.sources.GeniusSearchSource
import dev.bmcreations.networking.api.provideRetrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface NetworkGraph {
    val librarySource: LibrarySource
    val storeFrontSource: StoreFrontSource
    val geniusSearchSource: GeniusSearchSource
}

class NetworkGraphImpl(
    appContext: Context,
    tokenProvider: TokenProvider
) : NetworkGraph {

    private val appleMusicRetrofitImpl by lazy {
        provideRetrofit(
            baseUrl = "${AppleMusicApi.BASE_URL}/v${AppleMusicApi.VERSION}/",
            tokenProvider = tokenProvider
        )
    }

    private val geniusApi by lazy {
        provideRetrofit(
            baseUrl = GeniusApi.BASE_URL,
            tokenProvider = tokenProvider,
            httpClient = OkHttpClient.Builder().apply {
                // token in headers
                this.addInterceptor {
                    val request = it.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${tokenProvider.getGeniusApiToken()}")
                        .build()
                    it.proceed(request)
                }

                // add logging interceptor last to view others interceptors
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor().also {
                        it.level = HttpLoggingInterceptor.Level.BODY
                    }
                    this.addInterceptor(logging)
                }
            }.build()
        )
    }

    override val geniusSearchSource: GeniusSearchSource = GeniusSearchSource(geniusApi)
    override val storeFrontSource: StoreFrontSource = StoreFrontSource(appleMusicRetrofitImpl)
    override val librarySource: LibrarySource = LibrarySource(appleMusicRetrofitImpl, storeFrontSource)
}
