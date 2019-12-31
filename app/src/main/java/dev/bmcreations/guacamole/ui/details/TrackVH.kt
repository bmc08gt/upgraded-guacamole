package dev.bmcreations.guacamole.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel
import dev.bmcreations.musickit.extensions.mediaId
import kotlinx.android.synthetic.main.album_track_entity.view.*
import kotlinx.android.synthetic.main.playlist_track_entity.view.equalizer as peq
import kotlinx.android.synthetic.main.playlist_track_entity.view.playlist_track_art
import kotlinx.android.synthetic.main.album_track_entity.view.equalizer as aeq

class TrackVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nowPlaying: NowPlayingViewModel? = null
    var onClick: ((TrackVH) -> Unit)? = null

    var entity: dev.bmcreations.guacamole.models.TrackEntity? = null
        set(value) {
            field = value
            value?.let { entity ->
                entity.populate(this@TrackVH, nowPlaying)
                entity.toMetadata().mediaId?.let {
                    itemView.setOnClickListener { onClick?.invoke(this) }
                }
            }
        }

    fun hideVisualization() {
        entity?.let {
            if (it.container?.isPlaylist == true) {
                itemView.playlist_track_art.removeTint()
                itemView.peq.stop()
                itemView.peq.gone()
            } else {
                itemView.track_number.visible()
                itemView.aeq.stop()
                itemView.aeq.invisible()
            }
        }
    }

    fun pauseVisualization() {
        entity?.let {
            if (it.container?.isPlaylist == true) {
                if (!itemView.peq.isVisible()) {
                    if (it.container?.isPlaylist == true) {
                        itemView.playlist_track_art.tint(itemView.context.colors[R.color.equalizer_album_overlay])
                        itemView.peq.visible()
                    }
                    itemView.peq.stop()
                }
            } else {
                if (!itemView.aeq.isVisible()) {
                    itemView.track_number.invisible()
                    itemView.aeq.visible()
                }
                itemView.aeq.stop()
            }
        }
    }

    fun visualizePlayback() {
        entity?.let {
            if (it.container?.isPlaylist == true) {
                if (!itemView.peq.isVisible()) {
                    itemView.playlist_track_art.tint(itemView.context.colors[R.color.equalizer_album_overlay])
                    itemView.peq.visible()
                }
                itemView.peq.animateBars()
            } else {
                if (!itemView.aeq.isVisible()) {
                    itemView.track_number.invisible()
                    itemView.aeq.visible()
                }
                itemView.aeq.animateBars()
            }
        }
    }

    companion object Factory {
        const val ALBUM = 0
        const val PLAYLIST = 1

        fun create(parent: ViewGroup, viewType: Int): TrackVH {
            return when (viewType) {
                ALBUM -> TrackVH(LayoutInflater.from(parent.context).inflate(R.layout.album_track_entity, parent, false))
                else -> TrackVH(LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_entity, parent, false))
            }
        }
    }
}
