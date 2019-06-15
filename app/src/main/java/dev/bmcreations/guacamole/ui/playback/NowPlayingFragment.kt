package dev.bmcreations.guacamole.ui.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel.State
import dev.bmcreations.musickit.networking.api.models.*
import kotlinx.android.synthetic.main.now_playing_mini.*
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

        play_pause.setOnClickListener { viewModel?.playPause() }
        observe()
    }

    private fun observe() {
        viewModel?.playState?.observe(viewLifecycleOwner, Observer { handlePlayState(it) })
        viewModel?.selectedTrack?.observe(viewLifecycleOwner, Observer { handleTrackSelection(it) })
    }

    private fun handlePlayState(state: State) {
        when (state) {
            is State.Uninitialized -> root.gone()
            else -> {
                root.visible()
                when (state) {
                    is State.Initializing -> {
                        root.play_pause.gone()
                        root.loading.visible()
                    }
                    is State.Playing -> {
                        root.loading.gone()
                        root.play_pause.visible().also {
                            root.play_pause.setImageResource(R.drawable.ic_baseline_pause_black_24dp)
                        }
                    }
                    is State.Paused -> {
                        root.loading.gone()
                        root.play_pause.visible().also {
                            root.play_pause.setImageResource(R.drawable.ic_play_arrow_gray_32dp)
                        }
                    }
                }
            }
        }
    }

    private fun handleTrackSelection(entity: TrackEntity?) {
        if (entity != null) {
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