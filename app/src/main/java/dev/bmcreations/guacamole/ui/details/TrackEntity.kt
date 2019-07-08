package dev.bmcreations.guacamole.ui.details

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.guacamole.extensions.visible
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

fun TrackEntity.populate(view: View) {
    when (this) {
        is PlaylistTrackEntity -> {
            view.track_name.text = this.track.attributes?.name
            view.track_artist.text = this.track.attributes?.artistName
            if (this.track.attributes?.isExplicit == true) {
                view.explicit.visible()
            } else {
                view.explicit.gone()
            }
            picasso {
                view.playlist_track_art.visible()
                it.cancelRequest(view.playlist_track_art)
                it.load(this.track.attributes?.artwork?.urlWithDimensions)
                    .resize(600, 600)
                    .placeholder(R.drawable.ic_music_fail)
                    .error(R.drawable.ic_music_fail)
                    .into(view.playlist_track_art)
            }
        }
        is AlbumTrackEntity -> {
            view.track_number.text = this.track.attributes?.trackNumber?.toString()
            view.track_name.text = this.track.attributes?.name
            if (this.track.attributes?.isExplicit == true) {
                view.explicit.visible()
            } else {
                view.explicit.gone()
            }
            if (this.toMetadata().mediaId == null) {
                view.track_number.alpha = 0.5f
                view.track_name.alpha = 0.5f
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
            picasso {
                view.track_art.visible()
                it.cancelRequest(view.track_art)
                it.load(this.track.attributes?.artwork?.urlWithDimensions)
                    .resize(600, 600)
                    .placeholder(R.drawable.ic_music_fail)
                    .error(R.drawable.ic_music_fail)
                    .into(view.track_art)
            }
        }
        is AlbumTrackEntity -> {
            view.track_name?.text = this.track.attributes?.name
            if (this.track.attributes?.isExplicit == true) {
                view.explicit.visible()
            } else {
                view.explicit.gone()
            }
            picasso {
                view.track_art.visible()
                it.cancelRequest(view.track_art)
                it.load(this.track.attributes?.artwork?.urlWithDimensions)
                    .resize(600, 600)
                    .placeholder(R.drawable.ic_music_fail)
                    .error(R.drawable.ic_music_fail)
                    .into(view.track_art)
            }
        }
    }
}