package dev.bmcreations.guacamole.ui.library.songs

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.OrientationHelper
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.ui.library.LibraryBaseFragment
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel
import dev.bmcreations.guacamole.ui.widgets.SpacesItemDecoration
import dev.bmcreations.guacamole.ui.widgets.addItemDecorations
import kotlinx.android.synthetic.main.fragment_library_songs.view.*

class LibrarySongsFragment : LibraryBaseFragment() {

    override val layoutResId: Int = R.layout.fragment_library_songs

    private val nowPlaying by lazy {
        activity?.let { a ->
            mediaState?.let {
                a.getViewModel { NowPlayingViewModel.create(it) }
            }
        }
    }

    val songs by lazy {
        LibrarySongsAdapter().apply {
            onSongClicked = {
               nowPlaying?.play(it)
            }
        }
    }

    override fun initView() {
        super.initView()

        root.songs.apply {
            val header = SpacesItemDecoration(10.dp, OrientationHelper.VERTICAL).apply {
                this.header = true
                this.topBottomOnly = true
            }
            val footer = SpacesItemDecoration(10.dp, OrientationHelper.VERTICAL).apply {
                this.bottomOnly = true
            }
            this.addItemDecorations(header, footer)
        }
        root.songs.adapter = songs

        enableToolbarTranslationEffects(false)
        showToolbarElevation(true)

        observe()
    }

    private fun observe() {
        vm?.songs?.observe(viewLifecycleOwner, Observer { songList ->
            songs.submitList(songList.map { Song(it) })
        })
    }
}
