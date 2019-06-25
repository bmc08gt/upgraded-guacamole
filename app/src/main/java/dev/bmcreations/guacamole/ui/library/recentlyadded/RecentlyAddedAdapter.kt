package dev.bmcreations.guacamole.ui.library.recentlyadded

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import dev.bmcreations.musickit.networking.NetworkState
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity


class RecentlyAddedAdapter : PagedListAdapter<RecentlyAddedEntity, RecentlyAddedVH>(RECENTLY_ADDED_DIFF_CALLBACK) {

    var onRecentClicked: ((RecentlyAddedVH) -> (Unit))? = null

    var networkState: NetworkState? = null
        set(value) {
            val prev = field
            val prevExtra = hasExtraRow()
            field = value
            val newExtra = hasExtraRow()
            if (prevExtra != newExtra) {
                if (prevExtra) {
                    notifyItemRemoved(itemCount)
                } else {
                    notifyItemInserted(itemCount)
                }
            } else if (newExtra && prev != field) {
                notifyItemChanged(itemCount - 1)
            }
        }

    private fun hasExtraRow(): Boolean = networkState != null && networkState !== NetworkState.LOADED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyAddedVH {
        return RecentlyAddedVH.create(parent, viewType)
            .apply { this.onClick = onRecentClicked }
    }

    override fun onBindViewHolder(holder: RecentlyAddedVH, position: Int) {
        holder.entity = getItem(position)
    }
}