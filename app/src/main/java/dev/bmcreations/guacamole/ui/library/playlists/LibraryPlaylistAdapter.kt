package dev.bmcreations.guacamole.ui.library.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.bmcreations.guacamole.models.LibraryPlaylist

class LibraryPlaylistAdapter  : ListAdapter<LibraryPlaylist, LibraryPlaylistVH>(LIBRARY_PLAYLIST_DIFF_CALLBACK) {

    var onPlaylistClicked: ((LibraryPlaylistVH) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryPlaylistVH {
        return LibraryPlaylistVH.create(parent, viewType)
            .apply { this.onClick = onPlaylistClicked }
    }

    override fun onBindViewHolder(holder: LibraryPlaylistVH, position: Int) {
        holder.entity = getItem(position)
    }
}
