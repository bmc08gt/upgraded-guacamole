package dev.bmcreations.guacamole.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.guacamole.extensions.uiScope
import dev.bmcreations.guacamole.ui.MainActivity
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import dev.bmcreations.musickit.networking.extensions.albumArtworkUrl
import dev.bmcreations.musickit.networking.extensions.albumName
import dev.bmcreations.musickit.networking.extensions.artistName
import dev.bmcreations.musickit.networking.extensions.songName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaNotificationManager(private val mediaPlaybackService: MediaPlaybackService) {

    var notificationManager: NotificationManager
        private set
    private var playAction: NotificationCompat.Action
    private var pauseAction: NotificationCompat.Action

    init {
        notificationManager = mediaPlaybackService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        playAction = NotificationCompat.Action(
            R.drawable.ic_play_arrow_gray_32dp,
            "Play",
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_PLAY)
        )

        pauseAction = NotificationCompat.Action(
            R.drawable.ic_baseline_pause_black_24dp,
            "Pause",
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_PAUSE)
        )
    }

    fun getNotification(song: TrackEntity, token: MediaSessionCompat.Token, state: Int, currentPosition: Long): Notification {
        val builder = buildNotification(song, token, state, currentPosition)
        return builder.build()
    }

    private fun buildNotification(song: TrackEntity, token: MediaSessionCompat.Token, state: Int,
                                  currentPosition: Long): NotificationCompat.Builder {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()

        val isPlaying = (state == PlaybackStateCompat.STATE_PLAYING) or (state == PlaybackStateCompat.STATE_BUFFERING)

        val builder = NotificationCompat.Builder(mediaPlaybackService, CHANNEL_ID)
        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_STOP)
                )
        )
            .setColor(mediaPlaybackService.colors[R.color.colorAccent])
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(createContentIntent())
            .setContentTitle(song.toMetadata().songName)
            .setContentText(song.toMetadata().artistName)
            .setLargeIcon(null)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mediaPlaybackService, PlaybackStateCompat.ACTION_STOP
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (state == PlaybackStateCompat.STATE_PLAYING && currentPosition != PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN) {
            builder.setWhen(System.currentTimeMillis() - currentPosition)
            builder.setShowWhen(true)
            builder.setUsesChronometer(true)
        } else {
            builder.setWhen(0)
            builder.setShowWhen(false)
            builder.setUsesChronometer(false)
        }

        //builder.addAction(previousAction)

        builder.addAction(if (isPlaying) pauseAction else playAction)

        //builder.addAction(nextAction)

        uiScope.launch(Dispatchers.IO) {
            picasso {
                val bitmap = it.load(song.toMetadata().albumArtworkUrl).get()
                uiScope.launch {
                    builder.setLargeIcon(bitmap)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                }
            }
        }

        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (null == notificationManager.getNotificationChannel(CHANNEL_ID)) {
            val name = "Playback"
            val description = "Playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(mediaPlaybackService, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(mediaPlaybackService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    companion object {
        private val TAG = MediaNotificationManager::class.java.simpleName
        private val CANONICAL_NAME = MediaNotificationManager::class.java.simpleName
        private val CHANNEL_ID = "$CANONICAL_NAME.channel"
        private const val REQUEST_CODE = 501
        const val NOTIFICATION_ID = 412
    }
}