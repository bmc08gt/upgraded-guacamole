package dev.bmcreations.guacamole.media

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.apple.android.music.playback.controller.MediaPlayerController
import com.apple.android.music.playback.model.MediaItemType
import com.apple.android.music.playback.model.PlaybackState
import com.apple.android.music.playback.queue.CatalogPlaybackQueueItemProvider
import dev.bmcreations.musickit.networking.api.models.AlbumTrackEntity
import dev.bmcreations.musickit.networking.api.models.PlaylistTrackEntity
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import kotlin.math.max
import kotlin.math.min

class MediaSessionManager(val context: Context,
                          private val player: MediaPlayerController,
                          private val musicRepository: MusicRepository)
    : MediaSessionCompat.Callback() {

    private var queueIdentifier: String? = ""
    private var queueItems: ArrayList<MediaSessionCompat.QueueItem> = ArrayList()
    private var queueIndex = -1
    private var song: TrackEntity? = null
    private val mediaSession: MediaSessionCompat
    private val playbackStateBuilder: PlaybackStateCompat.Builder
    private var shuffle = false
    val sessionToken: MediaSessionCompat.Token
        get() {
            return mediaSession.sessionToken
        }
    val currentTrackMediaId: String?
        get() {
            return player.currentContainerStoreId ?: song?.let {
                when (it) {
                    is PlaylistTrackEntity -> it.track.id
                    is AlbumTrackEntity -> it.track.id
                }
            }
        }

    init {
        mediaSession = MediaSessionCompat(context, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)

            playbackStateBuilder = PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)

            setPlaybackState(playbackStateBuilder.build())

            setCallback(this@MediaSessionManager)
        }
    }

    private fun isReadyToPlay() = queueItems.isNotEmpty()

    private fun isNotReadyToPlay() = !isReadyToPlay()

    fun onDestroy() {
        mediaSession.release()
    }

    fun updatePlaybackState(currentState: Int) {
        playbackStateBuilder.setState(currentState, player.currentPosition, player.playbackRate)
        playbackStateBuilder.setBufferedPosition(player.bufferedPosition)
        playbackStateBuilder.setActions(allowedActions(player))
        mediaSession.setPlaybackState(playbackStateBuilder.build())
        mediaSession.isActive = currentState != PlaybackState.STOPPED
    }

    fun updateQueue(entity: TrackEntity?) {
        entity?.let { e ->
            when (e) {
                is PlaylistTrackEntity -> {
                    e.track.id?.let {
                        queueIndex = queueItems.indexOfFirst { item -> item.description.mediaId == it }
                        this.song = e
                            updateQueue()
                    }
                }
                is AlbumTrackEntity -> {
                    e.track.id?.let {
                        queueIndex = queueItems.indexOfFirst { item -> item.description.mediaId == it }
                        this.song = e
                        updateQueue()
                    }
                }
            }
        }
    }

    private fun updateQueue() {
        if (shuffle) {
            mediaSession.setQueue(queueItems.subList(queueIndex, queueIndex))
        }
        else {
            val minIndex = max(0, queueIndex - 5)
            val maxIndex = min(queueItems.size - 1, queueIndex + 5)
            mediaSession.setQueue(queueItems.subList(minIndex, maxIndex))
        }
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
        description?.let {
            queueItems.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            queueIndex = if (queueIndex == -1) 0 else queueIndex
            updateQueue()
        }
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
        description?.let {
            val index = queueItems.indexOfFirst { it.description.mediaId == description.mediaId }
            queueItems.removeAt(index)
            queueIndex = if (queueItems.isEmpty()) -1 else queueIndex
            updateQueue()
        }
    }

    override fun onPrepare() {
        if (queueIndex < 0 || isNotReadyToPlay()) return

        val mediaId = queueItems[queueIndex].description.mediaId
        mediaId?.let {
            song = musicRepository.getTrack(mediaId)
            mediaSession.setMetadata(song?.toMetadata())
            if (!mediaSession.isActive) mediaSession.isActive = true
        }
    }

    override fun onPlay() {
        if (isNotReadyToPlay()) return

        if (null == song) {
            onPrepare()
            val trackMetadata = song?.toMetadata()
            trackMetadata?.let {
                val queueProviderBuilder = CatalogPlaybackQueueItemProvider.Builder()
                val tracksIds = queueItems.map { it.description.mediaId }
                queueProviderBuilder.items(MediaItemType.SONG, *tracksIds.toTypedArray())
                queueProviderBuilder.startItemIndex(queueIndex)
                player.prepare(queueProviderBuilder.build(), true)
                player.play()
            }
        }
        else {
            player.play()
        }
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        queueIndex = queueItems.indexOfFirst { it.description.mediaId == mediaId }
        song = null
        onPlay()
    }

    override fun onPause() {
        player.pause()
    }

    override fun onStop() {
        player.stop()
        mediaSession.isActive = false
    }

    override fun onSkipToNext() {
        queueIndex = if (shuffle) {
            (0 until queueItems.size).random()
        } else {
            if (++queueIndex < queueItems.size) queueIndex else queueItems.size - 1
        }

        song = null
        onPlay()
    }

    override fun onSkipToPrevious() {
        if (player.currentPosition < FIVE_SECONDS_IN_MILLIS) {
            queueIndex = if (shuffle) {
                (0 until queueItems.size).random()
            } else {
                if (--queueIndex > 0) queueIndex else 0
            }
        }
        song = null
        onPlay()
    }

    override fun onSeekTo(position: Long) {
        player.seekToPosition(position)
    }

    override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
        when (command) {
            COMMAND_SWAP_QUEUE -> {
                val queueIdentifierExtra = extras?.getString(EXTRA_QUEUE_IDENTIFIER, "") ?: ""

                if (queueIdentifierExtra.isEmpty() or (queueIdentifierExtra != queueIdentifier)) {
                    queueItems.clear()
                    mediaSession.setQueue(queueItems)
                    queueIndex = -1
                    song = null
                    cb?.send(RESULT_ADD_QUEUE_ITEMS, null)
                }
                else {
                    cb?.send(RESULT_OK, null)
                }

                shuffle = false
                queueIdentifier = queueIdentifierExtra
            }
            COMMAND_GET_CURRENT_TRACK_ELAPSED_TIME -> {
                val bundle = Bundle()
                bundle.putLong(EXTRA_CURRENT_TRACK_ELAPSED_TIME, player.currentPosition)
                cb?.send(RESULT_CURRENT_TRACK_ELAPSED_TIME, bundle)
            }
            COMMAND_GET_CURRENT_TRACK -> {
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_CURRENT_TRACK, song)
                bundle.putString(EXTRA_CURRENT_PLAYLIST_ID, queueIdentifier)
                cb?.send(RESULT_CURRENT_TRACK, bundle)
            }
            COMMAND_GET_TRACK -> {
                extras?.let {
                    val mediaId: String? = it.getString(EXTRA_TRACK_ID, null)
                    if (null != mediaId) {
                        val bundle = Bundle()
                        val song = musicRepository.getTrack(mediaId)
                        bundle.putParcelable(EXTRA_TRACK, song)
                        cb?.send(RESULT_TRACK, bundle)
                    }
                    else {
                        cb?.send(RESULT_ERROR, null)
                    }
                }
            }
            COMMAND_STOP -> {
                onStop()
                cb?.send(RESULT_OK, null)
            }
        }
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        shuffle = shuffleMode != PlaybackStateCompat.SHUFFLE_MODE_NONE
    }
    companion object {
        private val TAG = MediaSessionManager::class.java.simpleName
        private val CANONICAL_NAME = MediaSessionManager::class.java.canonicalName
        const val FIVE_SECONDS_IN_MILLIS = 5 * 1000

        val COMMAND_SWAP_QUEUE = "$CANONICAL_NAME.command_swap_queue"
        val COMMAND_GET_TRACK = "$CANONICAL_NAME.command_get_track"
        val COMMAND_GET_CURRENT_TRACK = "$CANONICAL_NAME.command_current_track"
        val COMMAND_GET_CURRENT_TRACK_ELAPSED_TIME = "$CANONICAL_NAME.command_current_track_elapsed_time"
        val COMMAND_STOP = "$CANONICAL_NAME.command_stop"

        val EXTRA_QUEUE_IDENTIFIER = "$CANONICAL_NAME.extra_queue_identifier"
        val EXTRA_CURRENT_TRACK_ELAPSED_TIME = "$CANONICAL_NAME.extra_current_track_elapsed_time"
        val EXTRA_CURRENT_TRACK = "$CANONICAL_NAME.extra_current_track"
        val EXTRA_TRACK_ID = "$CANONICAL_NAME.extra_track_id"
        val EXTRA_CURRENT_PLAYLIST_ID = "$CANONICAL_NAME.extra_current_playlist_id"
        val EXTRA_TRACK = "$CANONICAL_NAME.extra_track"

        const val RESULT_ERROR = 0
        const val RESULT_ADD_QUEUE_ITEMS = 1
        const val RESULT_CURRENT_TRACK_ELAPSED_TIME = 2
        const val RESULT_CURRENT_TRACK = 3
        const val RESULT_TRACK = 4
        const val RESULT_OK = 5

        private fun allowedActions(playerController: MediaPlayerController): Long {
            var result = PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            when (playerController.playbackState) {
                PlaybackState.PLAYING -> result = result or PlaybackStateCompat.ACTION_PAUSE
                PlaybackState.PAUSED -> result =
                    result or (PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
                PlaybackState.STOPPED -> result = result or PlaybackStateCompat.ACTION_PLAY
            }
            return result
        }
    }
}