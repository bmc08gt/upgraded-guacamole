package dev.bmcreations.guacamole.ui.details

import androidx.recyclerview.widget.DiffUtil
import dev.bmcreations.musickit.networking.api.models.*

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