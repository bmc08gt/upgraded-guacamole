package dev.bmcreations.guacamole.ui.details

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.musickit.networking.api.models.*
import kotlinx.android.synthetic.main.now_playing_mini.view.*

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
                    .resize(72, 72)
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
                    .resize(72, 72)
                    .placeholder(R.drawable.ic_music_fail)
                    .error(R.drawable.ic_music_fail)
                    .into(view.track_art)
            }
        }
    }
}