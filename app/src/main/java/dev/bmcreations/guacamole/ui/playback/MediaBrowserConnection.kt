package dev.bmcreations.guacamole.ui.playback

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.bmcreations.guacamole.media.MediaBrowserController
import dev.bmcreations.guacamole.media.MediaSessionManager

class MediaBrowserConnection(context: Context,
                             private val cb: (Bundle?, Int) -> Unit,
                             private val itemsCb: (List<MediaBrowserCompat.MediaItem>) -> Unit) : MediaBrowserController(context) {

    override fun onConnected(mediaController: MediaControllerCompat) {
        mediaController.let { mc ->
            mc.sendCommand(MediaSessionManager.COMMAND_GET_CURRENT_TRACK, null, object: ResultReceiver(null) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    if (resultCode == MediaSessionManager.RESULT_CURRENT_TRACK) {
                        resultData?.let { bundle ->
                            cb.invoke(bundle, mc.playbackState.state)
                        }
                    }
                }
            })
        }
    }

    override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
        subscribe(parentId)
        itemsCb.invoke(children)
    }
}