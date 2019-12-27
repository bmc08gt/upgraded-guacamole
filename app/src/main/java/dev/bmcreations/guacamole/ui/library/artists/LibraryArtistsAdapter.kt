package dev.bmcreations.guacamole.ui.library.artists

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class LibraryArtistsAdapter  : ListAdapter<Artist, LibraryArtistVH>(LIBRARY_ARTIST_DIFF_CALLBACK) {

    var onArtistClicked: ((Artist) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryArtistVH {
        return LibraryArtistVH.create(parent, viewType)
            .apply { this.onClick = onArtistClicked }
    }

    override fun onBindViewHolder(holder: LibraryArtistVH, position: Int) {
        holder.entity = getItem(position)
    }
}

val LIBRARY_ARTIST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean = oldItem == newItem
}
