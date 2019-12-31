package dev.bmcreations.guacamole.ui.playback

import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.extensions.gone
import dev.bmcreations.guacamole.extensions.visible
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.media.State
import dev.bmcreations.guacamole.ui.details.populateMiniPlayer
import kotlinx.android.synthetic.main.now_playing_mini.*
import kotlinx.android.synthetic.main.now_playing_mini.view.*

class NowPlayingFragment : Fragment() {

    private val mediaState get() = context?.graph()?.sessionGraph?.mediaState

    private val viewModel by lazy {
        activity?.let { a ->
            mediaState?.let {
                a.getViewModel { NowPlayingViewModel.create(it) }
            }
        }
    }

    private lateinit var root: View

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

        activity?.volumeControlStream = AudioManager.STREAM_MUSIC

        observe()
    }

    private fun observe() {
        mediaState?.playState?.observe(viewLifecycleOwner, Observer { handlePlayState(it) })
        mediaState?.currentTrack?.observe(viewLifecycleOwner, Observer { it?.populateMiniPlayer(root) })
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
                    is State.InitializationFailed,
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
}
