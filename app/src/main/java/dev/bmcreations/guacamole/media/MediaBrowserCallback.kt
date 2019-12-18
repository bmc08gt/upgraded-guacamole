package dev.bmcreations.guacamole.media

import android.os.Bundle
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaBrowserCallback(
    private val connection: MediaBrowserController?,
    private val cb: (Bundle?, Int) -> Unit
) : MediaControllerCompat.Callback(), AnkoLogger {

    private val handler = android.os.Handler(Looper.getMainLooper())

    private var lastState: Int? = null

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let {
            connection?.mediaController?.let { mc ->
                mc.sendCommand(
                    MediaSessionManager.COMMAND_GET_CURRENT_TRACK,
                    null,
                    object : ResultReceiver(handler) {
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
        playbackState?.let {
            if (lastState != it.state) {
                cb.invoke(null, it.state)
                lastState = it.state
            }
        }
    }
}

val Int.playbackStateString: String
    get() = when (this) {
        PlaybackStateCompat.STATE_NONE -> "STATE_NONE"
        PlaybackStateCompat.STATE_BUFFERING -> "STATE_BUFFERING"
        PlaybackStateCompat.STATE_PLAYING -> "STATE_PLAYING"
        PlaybackStateCompat.STATE_PAUSED -> "STATE_PAUSED"
        PlaybackStateCompat.STATE_STOPPED -> "STATE_STOPPED"
        else -> "$this"
    }
