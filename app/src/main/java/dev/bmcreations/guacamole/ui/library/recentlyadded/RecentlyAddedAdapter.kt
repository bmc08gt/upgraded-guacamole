package dev.bmcreations.guacamole.ui.library.recentlyadded

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity

class RecentlyAddedAdapter : ListAdapter<RecentlyAddedEntity, RecentlyAddedVH>(RECENTLY_ADDED_DIFF_CALLBACK) {

    var onRecentClicked: ((RecentlyAddedVH) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyAddedVH {
        return RecentlyAddedVH.create(parent, viewType)
            .apply { this.onClick = onRecentClicked }
    }

    override fun onBindViewHolder(holder: RecentlyAddedVH, position: Int) {
        holder.entity = getItem(position)
    }
}