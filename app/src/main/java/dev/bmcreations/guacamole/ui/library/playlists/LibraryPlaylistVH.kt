package dev.bmcreations.guacamole.ui.library.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.models.LibraryPlaylist
import dev.bmcreations.guacamole.models.curator
import dev.bmcreations.guacamole.models.urlWithDimensions
import kotlinx.android.synthetic.main.library_playlist_row_entity.view.*

class LibraryPlaylistVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClick: ((LibraryPlaylistVH) -> Unit)? = null

    var entity: LibraryPlaylist? = null
        set(value) {
            field = value
            value?.let { playlist ->
                itemView.playlist_name.text = playlist.attributes?.name
                itemView.playlist_artists.text = playlist.attributes?.curator
                playlist.attributes?.let {
                    itemView.playlist_art.load(it.artwork?.urlWithDimensions) {
                        crossfade(true)
                        error(R.drawable.ic_music_fail)
                        size(600, 600)
                    }
                }
                ViewCompat.setTransitionName(itemView.playlist_art, "imageView-$adapterPosition")
                itemView.setOnClickListener { onClick?.invoke(this) }
            }
        }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): LibraryPlaylistVH {
            return LibraryPlaylistVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.library_playlist_row_entity,
                    parent,
                    false
                )
            )
        }
    }
}

val LIBRARY_PLAYLIST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<LibraryPlaylist>() {
    override fun areItemsTheSame(oldItem: LibraryPlaylist, newItem: LibraryPlaylist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryPlaylist, newItem: LibraryPlaylist): Boolean {
        return oldItem == newItem
    }
}

