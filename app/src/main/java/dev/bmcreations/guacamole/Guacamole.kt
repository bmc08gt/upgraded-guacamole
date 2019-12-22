package dev.bmcreations.guacamole

import android.app.Application
import android.content.Context
import dev.bmcreations.guacamole.auth.TokenExpiredCallback
import dev.bmcreations.guacamole.graphs.AppGraph
import dev.bmcreations.guacamole.graphs.NetworkGraphImpl
import dev.bmcreations.guacamole.graphs.SessionGraphImpl
import dev.bmcreations.musickit.auth.InvalidTokenReason

class Guacamole: Application() {

    private val expiredCallback: TokenExpiredCallback = object : TokenExpiredCallback {
        override fun onTokenExpired(reason: InvalidTokenReason) {
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
        val sessionGraph = SessionGraphImpl(this, expiredCallback)
        AppGraph(
            sessionGraph = sessionGraph,
            networkGraph = NetworkGraphImpl(
                appContext = this,
                tokenProvider = sessionGraph.tokenProvider
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        appGraph // touch the app graph
    }
}

fun Context.graph(): AppGraph {
    return (this.applicationContext as Guacamole).appGraph
}
