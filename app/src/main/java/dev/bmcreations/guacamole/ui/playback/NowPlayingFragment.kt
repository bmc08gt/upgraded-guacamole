package dev.bmcreations.guacamole.ui.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.picasso
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.guacamole.ui.details.AlbumTrackEntity
import dev.bmcreations.guacamole.ui.details.PlaylistTrackEntity
import dev.bmcreations.musickit.networking.api.models.isExplicit
import dev.bmcreations.musickit.networking.api.models.urlWithDimensions
import kotlinx.android.synthetic.main.now_playing_mini.view.*

class NowPlayingFragment: Fragment() {

    val viewModel by lazy {
        activity?.let { a -> a.getViewModel { NowPlayingViewModel.create(a) } }
    }

    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.now_playing_mini, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observe()
    }

    private fun observe() {
        viewModel?.selectedTrack?.observe(viewLifecycleOwner, Observer { entity ->
            when (entity) {
                null -> root.gone()
                else -> {
                    root.visible().also {
                        when (entity) {
                            is PlaylistTrackEntity -> {
                                root.track_name.text = entity.track.attributes?.name
//                                    track_artist.text = entity.track.attributes?.artistName
                                if (entity.track.attributes?.isExplicit == true) {
                                    root.explicit.visible()
                                } else {
                                    root.explicit.gone()
                                }
                                picasso {
                                    root.track_art.visible()
                                    it.cancelRequest(root.track_art)
                                    it.load(entity.track.attributes?.artwork?.urlWithDimensions)
                                        .resize(72, 72)
                                        .placeholder(R.drawable.ic_music_fail)
                                        .error(R.drawable.ic_music_fail)
                                        .into(root.track_art)
                                }
                            }
                            is AlbumTrackEntity -> {
                                root.track_name?.text = entity.track.attributes?.name
                                if (entity.track.attributes?.isExplicit == true) {
                                    root.explicit.visible()
                                } else {
                                    root.explicit.gone()
                                }
                                picasso {
                                    root.track_art.visible()
                                    it.cancelRequest(root.track_art)
                                    it.load(entity.track.attributes?.artwork?.urlWithDimensions)
                                        .resize(72, 72)
                                        .placeholder(R.drawable.ic_music_fail)
                                        .error(R.drawable.ic_music_fail)
                                        .into(root.track_art)
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}