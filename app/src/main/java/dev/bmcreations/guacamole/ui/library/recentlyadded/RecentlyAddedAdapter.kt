package dev.bmcreations.guacamole.ui.library.recentlyadded

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.ListAdapter
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.networking.NetworkState


class RecentlyAddedAdapter : ListAdapter<RecentlyAddedEntity, RecentlyAddedVH>(RECENTLY_ADDED_DIFF_CALLBACK) {

    var onRecentClicked: ((RecentlyAddedVH) -> (Unit))? = null

    override fun getItemCount(): Int {
        return super.getItemCount().coerceAtMost(60)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyAddedVH {
        return RecentlyAddedVH.create(parent, viewType)
            .apply { this.onClick = onRecentClicked }
    }

    override fun onBindViewHolder(holder: RecentlyAddedVH, position: Int) {
        holder.entity = getItem(position)
    }
}
