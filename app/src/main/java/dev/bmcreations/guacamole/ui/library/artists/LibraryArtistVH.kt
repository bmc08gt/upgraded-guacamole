package dev.bmcreations.guacamole.ui.library.artists

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import kotlinx.android.synthetic.main.library_artist_row_entity.view.*

data class Artist(val name: String?, val url: String? = null)

class LibraryArtistVH(itemView: View): RecyclerView.ViewHolder(itemView) {
    var onClick: ((Artist) -> Unit)? = null

    var entity: Artist? = null
        set(value) {
            field = value
            value?.let { artist ->
                itemView.artist_image.load(artist.url ?: "") {
                    placeholder(R.drawable.empty_artist_placeholder)
                    error(R.drawable.empty_artist_placeholder)
                    transformations(
                        CircleCropTransformation(),
                        RoundedCornersTransformation(40.dp(itemView.context).toFloat())
                    )
                }
                itemView.artist_name.text = artist.name
//                ViewCompat.setTransitionName(itemView.playlist_art, "imageView-$adapterPosition")
                itemView.setOnClickListener { onClick?.invoke(artist) }
            }
        }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): LibraryArtistVH {
            return LibraryArtistVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.library_artist_row_entity,
                    parent,
                    false
                )
            )
        }
    }
}
