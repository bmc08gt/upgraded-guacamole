package dev.bmcreations.guacamole.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.extensions.mediaId
import kotlinx.android.synthetic.main.album_track_entity.view.*
import kotlinx.android.synthetic.main.playlist_track_entity.view.*
import kotlinx.android.synthetic.main.playlist_track_entity.view.explicit
import kotlinx.android.synthetic.main.playlist_track_entity.view.playlist_track_art
import kotlinx.android.synthetic.main.playlist_track_entity.view.track_name

class TrackVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClick: ((TrackVH) -> Unit)? = null

    var entity: TrackEntity? = null
        set(value) {
            field = value
            value?.let {
                it.populate(itemView)
                it.toMetadata().mediaId?.let {
                    itemView.setOnClickListener { onClick?.invoke(this) }
                }
            }
        }

    companion object Factory {
        const val ALBUM = 0
        const val PLAYLIST = 1

        fun create(parent: ViewGroup, viewType: Int): TrackVH {
            return when (viewType) {
                ALBUM -> TrackVH(LayoutInflater.from(parent.context).inflate(R.layout.album_track_entity, parent, false))
                else -> TrackVH(LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_entity, parent, false))
            }
        }
    }
}