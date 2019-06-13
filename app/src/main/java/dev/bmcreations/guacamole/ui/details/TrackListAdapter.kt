package dev.bmcreations.guacamole.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.musickit.networking.api.models.LibraryAlbum
import kotlinx.android.synthetic.main.track_entity.view.*

class TrackListAdapter : ListAdapter<LibraryAlbum.Relationships.Tracks.Data, TrackVH>(TRACK_DATA_DIFF_CALLBACK) {

    var onTrackSelected: ((TrackVH) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVH {
        return TrackVH.create(parent, viewType)
            .apply { this.onClick = onTrackSelected }
    }

    override fun onBindViewHolder(holder: TrackVH, position: Int) {
        holder.entity = getItem(position)
    }
}

class TrackVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClick: ((TrackVH) -> Unit)? = null

    var entity: LibraryAlbum.Relationships.Tracks.Data? = null
        set(value) {
            field = value
            value?.let { entity ->
                itemView.track_number.text = entity.attributes?.trackNumber?.toString()
                itemView.track_name.text = entity.attributes?.name
                if (entity.attributes?.isExplicit == true) {
                    itemView.explicit.visible()
                } else {
                    itemView.explicit.gone()
                }

                itemView.setOnClickListener { onClick?.invoke(this) }
            }
        }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): TrackVH {
            return TrackVH(LayoutInflater.from(parent.context).inflate(R.layout.track_entity, parent, false))
        }
    }
}

val TRACK_DATA_DIFF_CALLBACK
        = object : DiffUtil.ItemCallback<LibraryAlbum.Relationships.Tracks.Data>() {
    override fun areItemsTheSame(oldItem: LibraryAlbum.Relationships.Tracks.Data,
                                 newItem: LibraryAlbum.Relationships.Tracks.Data): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryAlbum.Relationships.Tracks.Data,
                                    newItem: LibraryAlbum.Relationships.Tracks.Data): Boolean {
        return oldItem == newItem
    }

}