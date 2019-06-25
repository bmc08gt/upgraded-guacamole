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
import com.apple.android.music.playback.model.MediaPlayerException
import com.apple.android.music.playback.model.PlaybackState
import com.apple.android.music.playback.model.PlayerQueueItem
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.musickit.networking.extensions.uiScope
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaPlaybackService : MediaBrowserServiceCompat(), MediaPlayerController.Listener {

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var player: MediaPlayerController
    private lateinit var musicRepository: MusicRepository
    private lateinit var mediaNotificationManager: MediaNotificationManager
    private var serviceInStartedState = false

    override fun onCreate() {
        super.onCreate()

        initPlayer()
        initMusicRepository()
        initMediaSessionManager()
        initMediaNotificationManager()
    }

    private fun initPlayer() {
        player = MediaPlayerControllerFactory.createLocalController(this.applicationContext, TokenProvider.with(this))
        player.addListener(this)
    }

    private fun initMediaSessionManager() {
        mediaSessionManager = MediaSessionManager(this, player, musicRepository)
        sessionToken = mediaSessionManager.sessionToken
    }

    private fun initMusicRepository() {
        musicRepository = MusicRepository.getInstance(TokenProvider.with(this))
    }

    private fun initMediaNotificationManager() {
        mediaNotificationManager = MediaNotificationManager(this)
    }

    private fun updateNotificationForItemChanged(currentItem: PlayerQueueItem) {
        var track: TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = musicRepository.getTrack(it) }
        uiScope.launch(Dispatchers.IO) {
            while (player.currentPosition == PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN) {}
            uiScope.launch {
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
        var track: TrackEntity? = null
        mediaSessionManager.currentTrackMediaId?.let { track = musicRepository.getTrack(it) }
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
        var track: TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = musicRepository.getTrack(it) }
        mediaSessionManager.updateQueue(track)
    }

    private fun moveServiceToStartedState() {
        var track: TrackEntity? = null
        mediaSessionManager.currentTrackMediaId?.let { track = musicRepository.getTrack(it) }
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
        musicRepository.loadMediaItems(parentId, result)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("Guacamole.root", null)
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

    override fun onPlaybackShuffleModeChanged(p0: MediaPlayerController, p1: Int) = Unit

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

    override fun onPlaybackError(p0: MediaPlayerController, p1: MediaPlayerException) = Unit

    override fun onPlaybackRepeatModeChanged(p0: MediaPlayerController, p1: Int) = Unit

    override fun onPlaybackQueueChanged(p0: MediaPlayerController, p1: MutableList<PlayerQueueItem>) = Unit

    override fun onBufferingStateChanged(playerController: MediaPlayerController, buffering: Boolean) {
        updatePlaybackState(playerController.playbackState, playerController.isBuffering)
    }

    override fun onMetadataUpdated(playerController: MediaPlayerController, currentItem: PlayerQueueItem) = Unit

    override fun onPlayerStateRestored(playerController: MediaPlayerController) = Unit

    override fun onPlaybackQueueItemsAdded(playerController: MediaPlayerController, queueInsertionType: Int, containerType: Int, itemType: Int) = Unit

    private fun sendItemChangedBroadcast(currentItem: PlayerQueueItem) {
        var track: TrackEntity? = null
        currentItem.item.subscriptionStoreId?.let { track = musicRepository.getTrack(it) }
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
    }
}
