package dev.bmcreations.guacamole.ui.library.groupings

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class LibraryGroupingAdapter : ListAdapter<LibraryGrouping, LibraryGroupingVH>(LIBRARY_GROUPING_DIFF_CALLBACK) {

    var onGroupingClicked: ((LibraryGroupingVH) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryGroupingVH {
        return LibraryGroupingVH.create(parent, viewType)
            .apply { this.onClick = onGroupingClicked }
    }

    override fun onBindViewHolder(holder: LibraryGroupingVH, position: Int) {
        holder.entity = getItem(position)
    }
}