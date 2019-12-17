package dev.bmcreations.guacamole.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import coil.Coil
import coil.api.get
import coil.api.load
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors
import dev.bmcreations.guacamole.extensions.strings
import dev.bmcreations.guacamole.ui.MainActivity
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import dev.bmcreations.musickit.networking.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MediaNotificationManager(private val mediaPlaybackService: MediaPlaybackService): AnkoLogger {

    val notificationManager: NotificationManager by lazy {
        (mediaPlaybackService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
            it.cancelAll()
        }
    }

    private val playAction: NotificationCompat.Action by lazy {
        NotificationCompat.Action(
            R.drawable.ic_play_arrow_gray_32dp,
            mediaPlaybackService.strings[R.string.media_play],
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_PLAY)
        )
    }

    private val pauseAction: NotificationCompat.Action by lazy {
        NotificationCompat.Action(
            R.drawable.ic_baseline_pause_black_24dp,
            mediaPlaybackService.strings[R.string.media_pause],
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_PAUSE)
        )
    }

    private val skipPrevious: NotificationCompat.Action by lazy {
        NotificationCompat.Action(
            R.drawable.ic_baseline_skip_previous_black_24dp,
            mediaPlaybackService.strings[R.string.media_skip_back],
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        )
    }

    private val skipNext: NotificationCompat.Action by lazy {
        NotificationCompat.Action(
            R.drawable.ic_baseline_skip_next_black_24dp,
            mediaPlaybackService.strings[R.string.media_skip_next],
            MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        )
    }


    fun getNotification(song: TrackEntity, token: MediaSessionCompat.Token, state: Int, currentPosition: Long): Notification {
        val builder = buildNotification(song, token, state, currentPosition)
        return builder.build()
    }

    private fun buildNotification(song: TrackEntity, token: MediaSessionCompat.Token, state: Int,
                                  currentPosition: Long): NotificationCompat.Builder {
        info { "buildNotification(song=${song.toMetadata().songName}, state=$state, position=$currentPosition)" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()

        val isPlaying = (state == PlaybackStateCompat.STATE_PLAYING) or (state == PlaybackStateCompat.STATE_BUFFERING)

        val builder = NotificationCompat.Builder(mediaPlaybackService, CHANNEL_ID)
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

        val controller = mediaPlaybackService.mediaSessionManager.mediaSession.controller
        val posInQueue = controller.queue.indexOfFirst { it.description.mediaId == song.toMetadata().description.mediaId }

        if (controller.repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE || posInQueue > 0) {
            builder.addAction(skipPrevious)
        }
        builder.addAction(if (isPlaying) pauseAction else playAction)
        if (controller.repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE || posInQueue < controller.queue.lastIndex) {
            builder.addAction(skipNext)
        }

        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(*IntArray(builder.mActions.size) { it })
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(mediaPlaybackService, PlaybackStateCompat.ACTION_STOP)
                )
        )

        Coil.load(mediaPlaybackService, song.toMetadata().albumArtworkUrl) {
            target { drawable ->
                builder.setLargeIcon(drawable.toBitmap())
                notificationManager.notify(NOTIFICATION_ID, builder.build())
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
