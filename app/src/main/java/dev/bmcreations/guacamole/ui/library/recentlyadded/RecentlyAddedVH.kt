package dev.bmcreations.guacamole.ui.library.recentlyadded

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity
import dev.bmcreations.musickit.networking.api.models.urlWithDimensions
import kotlinx.android.synthetic.main.recently_added_entity.view.*

class RecentlyAddedVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClick: ((RecentlyAddedVH) -> Unit)? = null

    var entity: RecentlyAddedEntity? = null
        set(value) {
            field = value
            value?.let { entity ->
                itemView.ra_name.text = entity.attributes?.name
                itemView.ra_name_artist.text = entity.attributes?.artistName
                entity.attributes?.artwork?.also { artwork ->
                    picasso { picasso ->
                        picasso.cancelRequest(itemView.ra_image)
                        picasso.load(artwork.urlWithDimensions)
                            .error(R.drawable.ic_music_fail)
                            .resize(600, 600)
                            .into(itemView.ra_image)
                    }
                }
                ViewCompat.setTransitionName(itemView.ra_image, "imageView-$adapterPosition")
                itemView.setOnClickListener { onClick?.invoke(this) }
            }
        }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): RecentlyAddedVH {
            return RecentlyAddedVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recently_added_entity,
                    parent,
                    false
                )
            )
        }
    }
}

val RECENTLY_ADDED_DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecentlyAddedEntity>() {
    override fun areItemsTheSame(oldItem: RecentlyAddedEntity, newItem: RecentlyAddedEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentlyAddedEntity, newItem: RecentlyAddedEntity): Boolean {
        return oldItem == newItem
    }
}