package dev.bmcreations.guacamole.media

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaBrowserConnection(context: Context,
                             private val cb: ((Bundle?, Int) -> Unit)? = null,
                             private val itemsCb: ((List<MediaBrowserCompat.MediaItem>) -> Unit)? = null) : MediaBrowserController(context), AnkoLogger {

    override fun onConnected(mediaController: MediaControllerCompat) {
        info { "connected" }
        mediaController.let { mc ->
            mc.sendCommand(MediaSessionManager.COMMAND_GET_CURRENT_TRACK, null, object: ResultReceiver(null) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    if (resultCode == MediaSessionManager.RESULT_CURRENT_TRACK) {
                        resultData?.let { bundle ->
                            cb?.invoke(bundle, mc.playbackState.state)
                        }
                    }
                }
            })
        }
    }

    override fun onDisconnected() {
        info { "disconnected" }
    }

    override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
        subscribe(parentId)
        itemsCb?.invoke(children)
    }
}
