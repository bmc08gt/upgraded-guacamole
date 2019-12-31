package dev.bmcreations.guacamole.extensions

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.MediaBrowserServiceCompat
import dev.bmcreations.guacamole.models.apple.TrackEntity


fun List<TrackEntity>?.getTrackByMetadataMediaId(
    id: String
): TrackEntity? = this?.find { it.toMetadata().mediaId == id }

fun List<TrackEntity>?.getTrackByMediaId(
    id: String
): TrackEntity? = this?.find { it.track.id == id }

fun List<TrackEntity>?.getTrackByCatalogId(
    id: String
): TrackEntity? = this?.find { it.track.attributes?.playParams?.catalogId == id }

fun List<TrackEntity>?.loadMediaItems(
    parentId: String,
    result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>
) {
    result.detach()
    result.sendResult(this?.map {
        val metadata = it.toMetadata()
        val item = MediaDescriptionCompat.Builder()
            .setMediaId(metadata.mediaId)
            .setTitle(metadata.songName)
            .setSubtitle(metadata.artistName)
            .setIconUri(Uri.parse(metadata.albumArtworkUrl))
            .setExtras(metadata.bundle)
            .setMediaUri(Uri.parse(metadata.fullArtworkUri)).build()

        MediaBrowserCompat.MediaItem(item, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE) }?.toMutableList()
    )
}
