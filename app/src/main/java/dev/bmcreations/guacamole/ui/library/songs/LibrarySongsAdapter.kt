package dev.bmcreations.guacamole.ui.library.songs

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.bmcreations.guacamole.models.apple.TrackEntity

class LibrarySongsAdapter: ListAdapter<Song, LibrarySongVH>(LIBRARY_SONG_DIFF_CALLBACK) {

    var onSongClicked: ((TrackEntity) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrarySongVH {
        return LibrarySongVH.create(parent, viewType)
            .apply { this.onClick = onSongClicked }
    }

    override fun onBindViewHolder(holder: LibrarySongVH, position: Int) {
        holder.entity = getItem(position)
    }
}

val LIBRARY_SONG_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem.track.track.id == newItem.track.track.id
    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem == newItem
}
