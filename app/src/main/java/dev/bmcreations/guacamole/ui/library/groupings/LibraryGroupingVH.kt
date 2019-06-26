package dev.bmcreations.guacamole.ui.library.groupings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.bmcreations.guacamole.R
import kotlinx.android.synthetic.main.library_grouping_entity.view.*

class LibraryGroupingVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClick: ((LibraryGroupingVH) -> Unit)? = null

    var entity: LibraryGrouping? = null
        set(value) {
            field = value
            value?.let { grouping ->
                itemView.label.text = grouping.name
                itemView.setOnClickListener { onClick?.invoke(this) }
            }
        }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): LibraryGroupingVH {
            return LibraryGroupingVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.library_grouping_entity,
                    parent,
                    false
                )
            )
        }
    }
}

val LIBRARY_GROUPING_DIFF_CALLBACK = object : DiffUtil.ItemCallback<LibraryGrouping>() {
    override fun areItemsTheSame(oldItem: LibraryGrouping, newItem: LibraryGrouping): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: LibraryGrouping, newItem: LibraryGrouping): Boolean {
        return oldItem == newItem
    }
}