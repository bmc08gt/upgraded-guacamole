package dev.bmcreations.guacamole.media

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.MediaBrowserServiceCompat
import com.apple.android.music.playback.controller.MediaPlayerController
import com.apple.android.music.playback.controller.MediaPlayerControllerFactory
import com.apple.android.music.playback.model.*
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.models.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaPlaybackService : CoroutineScope by CoroutineScope(Dispatchers.IO), MediaBrowserServiceCompat(), MediaPlayerController.Listener {

    lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var player: MediaPlayerController
    private lateinit var mediaNotificationManager: MediaNotificationManager
    private var serviceInStartedState = false

    private val tokenProvider get() = graph().sessionGraph.tokenProvider
    private val mediaQueue get() = graph().sessionGraph.musicQueue

    override fun onCreate() {
        super.onCreate()

        initPlayer()
        initMediaSessionManager()
        initMediaNotificationManager()
    }

    private fun initPlayer() {
        player = MediaPlayerControllerFactory.createLocalController(this.applicationContext, tokenProvider)
        player.addListener(this)
    }

    private fun initMediaSessionManager() {
        mediaSessionManager = MediaSessionManager(this, player, mediaQueue)
        sessionToken = mediaSessionManager.sessionToken
    }

    private fun initMediaNotificationManager() {
        mediaNotificationManager = MediaNotificationManager(this)
    }

    private fun updateNotificationForItemChanged(currentItem: PlayerQueueItem) {
        var track: dev.bmcreations.guacamole.models.TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = mediaQueue.getTrackByCatalogId(it) }
        launch {
            while (player.currentPosition == PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN) {}
            launch(Dispatchers.Main) {
                track?.let {
                    val notification = mediaNotificationManager.getNotification(it, mediaSessionManager.sessionToken,
                        PlaybackStateCompat.STATE_PLAYING, player.currentPosition)
                    mediaNotificationManager.notificationManager
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
                }
            }
        }
    }

    private fun updateNotificationForPause() {
        var track: dev.bmcreations.guacamole.models.TrackEntity? = null
        mediaSessionManager.currentTrackMediaId?.let { track = mediaQueue.getTrackByMediaId(it) }
        track?.let {
            stopForeground(false)
            val notification = mediaNotificationManager.getNotification(it, mediaSessionManager.sessionToken,
                PlaybackStateCompat.STATE_PAUSED, player.currentPosition)
            mediaNotificationManager.notificationManager
                .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        mediaSessionManager.onDestroy()
    }

    private fun updateQueue(currentItem: PlayerQueueItem) {
        var track: dev.bmcreations.guacamole.models.TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = mediaQueue.getTrackByCatalogId(it) }
        mediaSessionManager.updateQueue(track)
    }

    private fun moveServiceToStartedState() {
        var track: dev.bmcreations.guacamole.models.TrackEntity? = null
        mediaSessionManager.currentTrackMediaId?.let { track = mediaQueue.getTrackByMediaId(it) }
        track?.let {
            val notification = mediaNotificationManager.getNotification(it, mediaSessionManager.sessionToken,
                PlaybackStateCompat.STATE_PLAYING, player.currentPosition)

            if (!serviceInStartedState) {
                ContextCompat.startForegroundService(this@MediaPlaybackService,
                    Intent(this@MediaPlaybackService, MediaPlaybackService::class.java)
                )
                serviceInStartedState = true
            }

            startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
        }
    }

    private fun moveServiceOutOfStartedState() {
        stopForeground(true)
        stopSelf()
        serviceInStartedState = false
    }

    private fun updatePlaybackState(@PlaybackState currentState: Int, buffering: Boolean) {
        val state = convertPlaybackState(currentState, buffering)
        mediaSessionManager.updatePlaybackState(state)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        mediaQueue.loadMediaItems(parentId, result)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        val extras = Bundle()
        extras.putBoolean("android.media.browse.CONTENT_STYLE_SUPPORTED", true)
        extras.putInt("android.media.browse.CONTENT_STYLE_BROWSABLE_HINT", 2)
        extras.putInt("android.media.browse.CONTENT_STYLE_PLAYABLE_HINT", 1)
        return BrowserRoot("Guacamole.root", extras)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onItemEnded(p0: MediaPlayerController, p1: PlayerQueueItem, p2: Long) = Unit

    override fun onCurrentItemChanged(p0: MediaPlayerController, p1: PlayerQueueItem?, p2: PlayerQueueItem?) {
        updatePlaybackState(p0.playbackState, p0.isBuffering)
        p2?.let {
            updateNotificationForItemChanged(it)
            sendItemChangedBroadcast(it)
            updateQueue(it)
        }
    }

    override fun onPlaybackShuffleModeChanged(p0: MediaPlayerController, p1: Int) {
        mediaSessionManager.mediaSession.setShuffleMode(convertShuffleMode(p1))
    }

    override fun onPlaybackStateUpdated(p0: MediaPlayerController) = Unit

    override fun onPlaybackStateChanged(p0: MediaPlayerController, p1: Int, p2: Int) {
        updatePlaybackState(p2, p0.isBuffering)

        when(convertPlaybackState(p2, p0.isBuffering)) {
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                moveServiceToStartedState()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                updateNotificationForPause()
            }
            PlaybackStateCompat.STATE_STOPPED -> {
                moveServiceOutOfStartedState()
            }
        }
    }

    override fun onPlaybackError(p0: MediaPlayerController, p1: MediaPlayerException) {
        info { p1 }
    }

    override fun onPlaybackRepeatModeChanged(p0: MediaPlayerController, p1: Int) {
        mediaSessionManager.mediaSession.setRepeatMode(convertRepeatMode(p1))
    }

    override fun onPlaybackQueueChanged(p0: MediaPlayerController, p1: MutableList<PlayerQueueItem>) = Unit

    override fun onBufferingStateChanged(playerController: MediaPlayerController, buffering: Boolean) {
        updatePlaybackState(playerController.playbackState, playerController.isBuffering)
    }

    override fun onMetadataUpdated(playerController: MediaPlayerController, currentItem: PlayerQueueItem) = Unit

    override fun onPlayerStateRestored(playerController: MediaPlayerController) = Unit

    override fun onPlaybackQueueItemsAdded(playerController: MediaPlayerController, queueInsertionType: Int, containerType: Int, itemType: Int) = Unit

    private fun sendItemChangedBroadcast(currentItem: PlayerQueueItem) {
        var track: dev.bmcreations.guacamole.models.TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = mediaQueue.getTrackByCatalogId(it) }
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intent = Intent(ACTION_CURRENT_ITEM_CHANGED)
        intent.putExtra(EXTRA_CURRENT_ITEM, track)
        localBroadcastManager.sendBroadcast(intent)
    }

    companion object : AnkoLogger {
        private val CANONICAL_NAME = MediaPlaybackService::class.java.canonicalName
        val ACTION_CURRENT_ITEM_CHANGED = "$CANONICAL_NAME.action_current_item_changed"
        val EXTRA_CURRENT_ITEM = "$CANONICAL_NAME.extra_current_item"

        init {
            try {
                System.loadLibrary("c++_shared")
                System.loadLibrary("appleMusicSDK")
            } catch (e: Exception) {
                info { "Could not load library due to: ${Log.getStackTraceString(e)}" }
                throw e
            }
        }

        private fun convertPlaybackState(@PlaybackState playbackState: Int, buffering: Boolean): Int {
            return when (playbackState) {
                PlaybackState.STOPPED -> PlaybackStateCompat.STATE_STOPPED
                PlaybackState.PAUSED -> PlaybackStateCompat.STATE_PAUSED
                PlaybackState.PLAYING -> if (buffering) PlaybackStateCompat.STATE_BUFFERING else PlaybackStateCompat.STATE_PLAYING
                else -> PlaybackStateCompat.STATE_NONE
            }
        }

        private fun convertRepeatMode(@PlaybackRepeatMode repeatMode: Int): Int {
            return when (repeatMode) {
                PlaybackRepeatMode.REPEAT_MODE_ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
                PlaybackRepeatMode.REPEAT_MODE_ONE -> PlaybackStateCompat.REPEAT_MODE_ONE
                PlaybackRepeatMode.REPEAT_MODE_OFF -> PlaybackStateCompat.REPEAT_MODE_NONE
                else -> PlaybackStateCompat.REPEAT_MODE_NONE
            }
        }


        private fun convertShuffleMode(@PlaybackShuffleMode shuffleMode: Int): Int {
            when (shuffleMode) {
                PlaybackShuffleMode.SHUFFLE_MODE_OFF -> return PlaybackStateCompat.SHUFFLE_MODE_NONE
                PlaybackShuffleMode.SHUFFLE_MODE_SONGS -> return PlaybackStateCompat.SHUFFLE_MODE_ALL
            }
            return PlaybackStateCompat.SHUFFLE_MODE_NONE
        }
    }
}
