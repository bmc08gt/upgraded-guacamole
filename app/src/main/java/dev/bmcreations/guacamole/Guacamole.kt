package dev.bmcreations.guacamole

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.Collections.singletonList

class Guacamole: Application() {

    override fun onCreate() {
        super.onCreate()
        OkHttpClient.Builder()
            .protocols(singletonList(Protocol.HTTP_1_1))
            .build().apply {
                Picasso.Builder(this@Guacamole)
                    .downloader(OkHttp3Downloader(this))
                    .loggingEnabled(BuildConfig.DEBUG)
                    .build().apply {
                        Picasso.setSingletonInstance(this)
                    }
            }
    }
}