package dev.bmcreations.guacamole.ui.library.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.musickit.networking.api.models.TrackEntity
import kotlinx.android.synthetic.main.library_song_row_entity.view.*
import kotlinx.android.synthetic.main.library_song_row_entity.view.equalizer
import kotlinx.android.synthetic.main.library_song_row_entity.view.track_name

data class Song(val track: TrackEntity)

class LibrarySongVH(itemView: View): RecyclerView.ViewHolder(itemView) {
    var onClick: ((TrackEntity) -> Unit)? = null

    var entity: Song? = null
        set(value) {
            field = value
            value?.let { song ->
                hideVisualization()

                itemView.track_art.load(song.track.container?.artwork ?: "") {
                    error(R.drawable.ic_music_fail)
                    placeholder(R.drawable.ic_music_fail)
                    transformations(
                        RoundedCornersTransformation(8.dp.toFloat())
                    )
                }
                itemView.track_name.text = song.track.track.attributes?.name
//                ViewCompat.setTransitionName(itemView.playlist_art, "imageView-$adapterPosition")

                itemView.setOnClickListener { onClick?.invoke(song.track) }
            }
        }

    fun hideVisualization() {
        entity?.let {
            itemView.track_art.removeTint()
            itemView.equalizer.stop()
            itemView.equalizer.gone()
        }
    }

    fun pauseVisualization() {
        entity?.let {
            if (!itemView.equalizer.isVisible()) {
                itemView.track_art.tint(itemView.context.colors[R.color.equalizer_album_overlay])
                itemView.equalizer.visible()
                itemView.equalizer.stop()
            }
        }
    }

    fun startVisualization() {
        entity?.let {
            if (!itemView.equalizer.isVisible()) {
                itemView.track_art.tint(itemView.context.colors[R.color.equalizer_album_overlay])
                itemView.equalizer.visible()
            }
            itemView.equalizer.animateBars()
        }
    }

    companion object Factory {
        fun create(parent: ViewGroup, viewType: Int): LibrarySongVH {
            return LibrarySongVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.library_song_row_entity,
                    parent,
                    false
                )
            )
        }
    }
}
