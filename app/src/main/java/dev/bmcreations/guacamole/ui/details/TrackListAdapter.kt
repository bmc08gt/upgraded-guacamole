package dev.bmcreations.guacamole.ui.details

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.bmcreations.guacamole.ui.details.TrackVH.Factory.ALBUM
import dev.bmcreations.guacamole.ui.details.TrackVH.Factory.PLAYLIST

class TrackListAdapter : ListAdapter<TrackEntity, TrackVH>(TRACK_DATA_DIFF_CALLBACK) {

    var onTrackSelected: ((TrackVH) -> (Unit))? = null

    override fun getItemViewType(position: Int): Int {
        return when (super.getItem(position)) {
            is AlbumTrackEntity -> ALBUM
            else -> PLAYLIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVH {
        return TrackVH.create(parent, viewType).apply { this.onClick = onTrackSelected }
    }

    override fun onBindViewHolder(holder: TrackVH, position: Int) {
        holder.entity = getItem(position)
    }
}