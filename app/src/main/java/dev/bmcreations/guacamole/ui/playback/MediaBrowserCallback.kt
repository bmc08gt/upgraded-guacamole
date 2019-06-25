package dev.bmcreations.guacamole.ui.playback

import android.os.Bundle
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.bmcreations.guacamole.media.MediaBrowserController
import dev.bmcreations.guacamole.media.MediaSessionManager
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaBrowserCallback(val connection: MediaBrowserController?, val cb: (Bundle?, Int) -> Unit): MediaControllerCompat.Callback(), AnkoLogger {

    val handler = android.os.Handler(Looper.getMainLooper())

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let {
            connection?.mediaController?.let { mc ->
                mc.sendCommand(MediaSessionManager.COMMAND_GET_CURRENT_TRACK, null, object: ResultReceiver(handler) {
                    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                        if (resultCode == MediaSessionManager.RESULT_CURRENT_TRACK) {
                            resultData?.let { bundle ->
                                mc.playbackState.errorMessage?.let { info { "error=$it" } }
                                cb.invoke(bundle, mc.playbackState.state)
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackStateCompat?) {
        info { "playbackStateChange(${playbackState?.state})" }
        playbackState?.let { cb.invoke(null, it.state) }
    }
}