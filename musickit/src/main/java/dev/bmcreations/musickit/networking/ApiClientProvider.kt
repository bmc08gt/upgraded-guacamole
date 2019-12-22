package dev.bmcreations.musickit.networking

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dev.bmcreations.musickit.BuildConfig
import dev.bmcreations.musickit.auth.TokenProviding
import dev.bmcreations.musickit.auth.toTokenReason
import dev.bmcreations.musickit.networking.api.music.CatalogService
import dev.bmcreations.musickit.networking.api.music.LibraryService
import dev.bmcreations.musickit.networking.api.music.StoreFrontService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideGson(): Gson {
    return GsonBuilder().create()
}

fun provideLibraryService(retrofit: Retrofit): LibraryService {
    return retrofit.create(LibraryService::class.java)
}

fun provideCatalogService(retrofit: Retrofit): CatalogService {
    return retrofit.create(CatalogService::class.java)
}

fun provideStoreFrontService(retrofit: Retrofit): StoreFrontService {
    return retrofit.create(StoreFrontService::class.java)
}

fun provideOkHttpClientBuilder(tokenProvider: TokenProviding): OkHttpClient.Builder {
    return OkHttpClient.Builder().apply {
        // token in headers
        this.addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Authorization", "Bearer ${tokenProvider.getDeveloperToken()}")
                .addHeader("Music-User-Token", tokenProvider.getUserToken())
                .build()
            it.proceed(request)
        }

        // token expired reauth
        this.addInterceptor {
            val request = it.request()
            val response = it.proceed(request)
            if (response.code == 403 || response.code == 401) {
                tokenProvider.onTokenExpired(response.code.toTokenReason())
            }
            response
        }

        // add logging interceptor last to view others interceptors
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            }
            this.addInterceptor(logging)
        }
    }
}

fun provideOkHttpClient(tokenProvider: TokenProviding): OkHttpClient {
    return provideOkHttpClientBuilder(tokenProvider).build()
}

fun provideRetrofit(
    gson: Gson = provideGson(),
    tokenProvider: TokenProviding,
    httpClient: OkHttpClient = provideOkHttpClient(tokenProvider),
    baseUrl: String
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(httpClient)
        .build()
}
