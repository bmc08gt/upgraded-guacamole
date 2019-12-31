package dev.bmcreations.musickit.queue

import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import dev.bmcreations.guacamole.models.TrackEntity
import dev.bmcreations.musickit.extensions.songName
import dev.bmcreations.musickit.networking.api.music.getTrackByCatalogId
import dev.bmcreations.musickit.networking.api.music.getTrackByMediaId
import dev.bmcreations.musickit.networking.api.music.getTrackByMetadataMediaId
import dev.bmcreations.musickit.networking.api.music.loadMediaItems
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MusicQueue : AnkoLogger {

    private var _tracks: MutableList<TrackEntity>? = mutableListOf()
    var tracks: MutableList<TrackEntity>? = mutableListOf()

    fun onTrackSelected(track: TrackEntity? = _tracks?.firstOrNull()) {
        info { "selected track=${track?.toMetadata()?.songName}" }
        if (_tracks != null && tracks != _tracks) {
            tracks = _tracks
            _tracks = null
        }
    }

    fun updateQueue(tracks: List<TrackEntity>?) {
        info { "swapping queue of ${_tracks?.count()} with ${tracks?.count()}" }
        _tracks = tracks?.toMutableList()
    }

    fun addToQueue(tracks: List<TrackEntity>) {
        info { "adding ${tracks.count()} tracks" }
        _tracks?.addAll(tracks)
    }

    fun removeFromQueue(tracks: List<TrackEntity>) {
        info { "removing ${tracks.count()} tracks" }
        _tracks?.removeAll(tracks)
    }

    fun getTrackByMetadataMediaId(id: String): TrackEntity? = tracks?.getTrackByMetadataMediaId(id)

    fun getTrackByMediaId(id: String): TrackEntity? = tracks?.getTrackByMediaId(id)

    fun getTrackByCatalogId(id: String): TrackEntity? = tracks?.getTrackByCatalogId(id)

    fun loadMediaItems(
        parentId: String,
        result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) = tracks?.loadMediaItems(parentId, result)
}
