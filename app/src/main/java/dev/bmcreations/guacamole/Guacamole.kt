package dev.bmcreations.guacamole

import android.app.Application
import android.content.Context
import android.content.Intent
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dev.bmcreations.guacamole.graphs.AppGraph
import dev.bmcreations.guacamole.graphs.NetworkGraphImpl
import dev.bmcreations.guacamole.graphs.SessionGraphImpl
import dev.bmcreations.guacamole.ui.MainActivity
import dev.bmcreations.musickit.networking.TokenExpiredCallback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.Collections.singletonList

class Guacamole: Application() {

    private val expiredCallback: TokenExpiredCallback = object :TokenExpiredCallback {
        override fun onTokenExpired() {
//            val sm = appGraph.sessionGraph.sessionManager
//            if (sm.)
//            appGraph.sessionGraph.sessionManager.removeUser {
//                startActivity(Intent(this@Guacamole, MainActivity::class.java).apply {
//                    flags = flags.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                })
//            }
        }
    }

    val appGraph by lazy {
        val sessionGraph = SessionGraphImpl(this)
        AppGraph(
            sessionGraph = sessionGraph,
            networkGraph = NetworkGraphImpl(this, expiryListener = expiredCallback))
    }

    override fun onCreate() {
        super.onCreate()
        appGraph // touch the app graph
        OkHttpClient.Builder()
            .protocols(singletonList(Protocol.HTTP_1_1))
            .build().apply {
                Picasso.Builder(this@Guacamole)
                    .downloader(OkHttp3Downloader(this))
                    .build().apply {
                        Picasso.setSingletonInstance(this)
                    }
            }
    }
}

fun Context.graph(): AppGraph {
    return (this.applicationContext as Guacamole).appGraph
}
