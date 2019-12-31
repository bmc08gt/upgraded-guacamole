package dev.bmcreations.guacamole.media

import android.content.*
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.bmcreations.guacamole.models.apple.TrackEntity

open class MediaBrowserController(val context: Context) {

    private interface CallbackCommand {
        fun perform(callback: MediaControllerCompat.Callback)
    }

    private val callbacks = ArrayList<MediaControllerCompat.Callback>()
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
    private val mediaControllerCallback: MediaControllerCallback = MediaControllerCallback()
    private var mediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
    private var playbackServiceBroadcastReceiver = PlaybackServiceBroadcastReceiver()
    private var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
        private set

    fun onStart() {
        if (null == mediaBrowser) {
            mediaBrowser = MediaBrowserCompat(context,
                ComponentName(context, MediaPlaybackService::class.java),
                mediaBrowserConnectionCallback,
                null)
            mediaBrowser?.connect()
        }

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(playbackServiceBroadcastReceiver, IntentFilter(MediaPlaybackService.ACTION_CURRENT_ITEM_CHANGED))
    }

    fun onStop() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(playbackServiceBroadcastReceiver)
        mediaController?.unregisterCallback(mediaControllerCallback)
        mediaController = null
        mediaBrowser?.disconnect()
        mediaBrowser = null
    }

    fun subscribe(parentId: String) {
        mediaBrowser?.subscribe(parentId, mediaBrowserSubscriptionCallback)
    }

    fun unsubscribe(parentId: String) {
        mediaBrowser?.unsubscribe(parentId, mediaBrowserSubscriptionCallback)
    }

    fun registerCallback(callback: MediaControllerCompat.Callback?) {
        callback?.let {
            callbacks.add(callback)
            mediaController?.let { controller ->
                controller.metadata?.let { metadata -> callback.onMetadataChanged(metadata) }
                controller.playbackState?.let { playbackState -> callback.onPlaybackStateChanged(playbackState) }
            }
        }
    }

    fun unregisterCallback(callback: MediaControllerCompat.Callback?) {
        callbacks.remove(callback)
    }

    private fun performOnAllCallbacks(command: CallbackCommand) {
        for (callback in callbacks) {
            command.perform(callback)
        }
    }

    protected open fun onConnected(mediaController: MediaControllerCompat) = Unit

    protected open fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) = Unit

    protected open fun onDisconnected() = Unit

    private inner class MediaBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            try {
                mediaController = MediaControllerCompat(context, mediaBrowser!!.sessionToken)
                mediaController?.let { mc ->
                    mc.registerCallback(mediaControllerCallback)
                    this@MediaBrowserController.onConnected(mc)
                }
            } catch (e: RemoteException) {
                throw RuntimeException(e)
            }

            mediaBrowser?.let { it.subscribe(it.root, mediaBrowserSubscriptionCallback) }
        }
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            performOnAllCallbacks(object : CallbackCommand {
                override fun perform(callback: MediaControllerCompat.Callback) {
                    callback.onMetadataChanged(metadata)
                }
            })
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            performOnAllCallbacks(object : CallbackCommand {
                override fun perform(callback: MediaControllerCompat.Callback) {
                    callback.onPlaybackStateChanged(state)
                }
            })
        }

        override fun onSessionDestroyed() {
            onPlaybackStateChanged(null)
            this@MediaBrowserController.onDisconnected()
        }
    }

    private inner class MediaBrowserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            this@MediaBrowserController.onChildrenLoaded(parentId, children)
        }
    }

    private inner class PlaybackServiceBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == MediaPlaybackService.ACTION_CURRENT_ITEM_CHANGED) {
                    it.extras?.let { extras ->
                        val song = extras.getParcelable(MediaPlaybackService.EXTRA_CURRENT_ITEM) as? TrackEntity
                        performOnAllCallbacks(object : CallbackCommand {
                            override fun perform(callback: MediaControllerCompat.Callback) {
                                callback.onMetadataChanged(song?.toMetadata())
                            }
                        })
                    }
                }
            }
        }
    }
}
