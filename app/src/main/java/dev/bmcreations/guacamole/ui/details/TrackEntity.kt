package dev.bmcreations.guacamole.ui.details

import androidx.recyclerview.widget.DiffUtil
import dev.bmcreations.musickit.networking.api.models.LibraryAlbum
import dev.bmcreations.musickit.networking.api.models.PlaylistTrack

sealed class TrackEntity {
    override fun equals(other: Any?): Boolean {
        return if (other is TrackEntity) {
            when (this) {
                is AlbumTrackEntity -> {
                    if (other is AlbumTrackEntity) {
                        this === other
                    } else {
                        false
                    }
                }
                is PlaylistTrackEntity -> {
                    if (other is PlaylistTrackEntity) {
                        this === other
                    } else {
                        false
                    }
                }
            }
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
data class PlaylistTrackEntity(val track: PlaylistTrack): TrackEntity()
data class AlbumTrackEntity(val track: LibraryAlbum.Relationships.Tracks.Data): TrackEntity()

val TRACK_DATA_DIFF_CALLBACK
        = object : DiffUtil.ItemCallback<TrackEntity>() {
    override fun areItemsTheSame(oldItem: TrackEntity,
                                 newItem: TrackEntity): Boolean {
        return when (oldItem) {
            newItem -> {
                if (oldItem is AlbumTrackEntity) {
                    oldItem.track.id == (newItem as AlbumTrackEntity).track.id
                } else {
                    (oldItem as PlaylistTrackEntity).track.id == (newItem as PlaylistTrackEntity).track.id
                }
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: TrackEntity,
                                    newItem: TrackEntity): Boolean {
        return oldItem == newItem
    }
}