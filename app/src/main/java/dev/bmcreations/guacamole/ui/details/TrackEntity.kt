package dev.bmcreations.guacamole.ui.details

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import coil.api.load
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel
import dev.bmcreations.musickit.networking.api.models.*
import dev.bmcreations.musickit.networking.extensions.mediaId
import kotlinx.android.synthetic.main.album_track_entity.view.*
import kotlinx.android.synthetic.main.now_playing_mini.view.*
import kotlinx.android.synthetic.main.now_playing_mini.view.explicit
import kotlinx.android.synthetic.main.now_playing_mini.view.track_name
import kotlinx.android.synthetic.main.playlist_track_entity.view.*

val TRACK_DATA_DIFF_CALLBACK
        = object : DiffUtil.ItemCallback<TrackEntity>() {
    override fun areItemsTheSame(oldItem: TrackEntity,
                                 newItem: TrackEntity): Boolean {
        return when (oldItem) {
            newItem -> {
                if (oldItem is AlbumTrackEntity) {
                    oldItem.track.id == (newItem as AlbumTrackEntity).track.id
                } else {
                    (oldItem as PlaylistTrackEntity).track.id == (newItem as PlaylistTrackEntity).track.id
                }
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: TrackEntity,
                                    newItem: TrackEntity): Boolean {
        return oldItem == newItem
    }
}

fun TrackEntity.populate(holder: TrackVH, nowPlaying: NowPlayingViewModel?) {
    holder.hideVisualization()
    val selected = nowPlaying?.selectedTrack?.value
    if (selected == this) {
        nowPlaying.playState.value?.let { state ->
            when (state) {
                NowPlayingViewModel.State.Playing -> holder.visualizePlayback()
                NowPlayingViewModel.State.Paused,
                NowPlayingViewModel.State.Initializing -> holder.pauseVisualization()
            }
        }
    }
    when (this) {
        is PlaylistTrackEntity -> {
            holder.itemView.track_name.text = this.track.attributes?.name
            holder.itemView.track_artist.text = this.track.attributes?.artistName
            if (this.track.attributes?.isExplicit == true) {
                holder.itemView.explicit.visible()
            } else {
                holder.itemView.explicit.gone()
            }
            holder.itemView.playlist_track_art.apply {
                visible()
                load(this@populate.track.attributes?.artwork?.urlWithDimensions) {
                    size(600, 600)
                    crossfade(true)
                    error(R.drawable.ic_music_fail)
                    placeholder(R.drawable.ic_music_fail)
                }
            }
        }
        is AlbumTrackEntity -> {
            holder.itemView.track_number.alpha = 1.0f
            holder.itemView.track_name.alpha = 1.0f
            holder.itemView.track_number.text = this.track.attributes?.trackNumber?.toString()
            holder.itemView.track_name.text = this.track.attributes?.name
            if (this.track.attributes?.isExplicit == true) {
                holder.itemView.explicit.visible()
            } else {
                holder.itemView.explicit.gone()
            }
            if (this.track.attributes?.playParams == null) {
                holder.itemView.track_number.alpha = 0.5f
                holder.itemView.track_name.alpha = 0.5f
            }
        }
    }
}

fun TrackEntity.populateMiniPlayer(view: View) {
    when (this) {
        is PlaylistTrackEntity -> {
            view.track_name.text = this.track.attributes?.name
            if (this.track.attributes?.isExplicit == true) {
                view.explicit.visible()
            } else {
                view.explicit.gone()
            }
            view.track_art.apply {
                visible()
                load(this@populateMiniPlayer.track.attributes?.artwork?.urlWithDimensions) {
                    size(600, 600)
                    crossfade(true)
                    error(R.drawable.ic_music_fail)
                    placeholder(R.drawable.ic_music_fail)
                }
            }
        }
        is AlbumTrackEntity -> {
            view.track_name?.text = this.track.attributes?.name
            if (this.track.attributes?.isExplicit == true) {
                view.explicit.visible()
            } else {
                view.explicit.gone()
            }
            view.track_art.apply {
                visible()
                load(this@populateMiniPlayer.track.attributes?.artwork?.urlWithDimensions) {
                    size(600, 600)
                    crossfade(true)
                    error(R.drawable.ic_music_fail)
                    placeholder(R.drawable.ic_music_fail)
                }
            }
        }
    }
}
