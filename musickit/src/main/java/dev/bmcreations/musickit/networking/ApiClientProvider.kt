package dev.bmcreations.musickit.networking

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dev.bmcreations.musickit.BuildConfig
import dev.bmcreations.musickit.networking.api.music.CatalogService
import dev.bmcreations.musickit.networking.api.music.LibraryService
import dev.bmcreations.musickit.networking.api.music.StoreFrontService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface TokenExpiredCallback {
    fun onTokenExpired()
}

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

fun provideOkHttpClientBuilder(expiredTokenCallback: TokenExpiredCallback? = null): OkHttpClient.Builder {
    return OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            }
            this.addInterceptor(logging)
        }

        this.addInterceptor {
            val request = it.request()
            val response = it.proceed(request)
            if (response.code == 403 || response.code == 401) {
                expiredTokenCallback?.onTokenExpired()
            }
            response
        }
    }
}

fun provideOkHttpClient(expiredTokenCallback: TokenExpiredCallback? = null): OkHttpClient {
    return provideOkHttpClientBuilder(expiredTokenCallback).build()
}

fun provideRetrofit(
    gson: Gson = provideGson(),
    expiredTokenCallback: TokenExpiredCallback? = null,
    httpClient: OkHttpClient = provideOkHttpClient(expiredTokenCallback),
    baseUrl: String
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(httpClient)
        .build()
}
