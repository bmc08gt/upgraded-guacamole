package dev.bmcreations.guacamole.ui.library.artists

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.OrientationHelper
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.ui.library.LibraryBaseFragment
import dev.bmcreations.guacamole.ui.widgets.SpacesItemDecoration
import dev.bmcreations.guacamole.ui.widgets.addItemDecorations
import kotlinx.android.synthetic.main.fragment_library_artists.view.*

class LibraryArtistsFragment: LibraryBaseFragment() {

    override val layoutResId: Int = R.layout.fragment_library_artists

    val artists by lazy {
        LibraryArtistsAdapter().apply {
            onArtistClicked = {
//                val extras = FragmentNavigatorExtras(it.itemView.playlist_art to it.itemView.playlist_art.transitionName)
//                val args = Bundle().apply {
//                    this.putString("album.id", it.entity?.id)
//                    this.putString("album.name", it.entity?.attributes?.name)
//                    this.putString("album.artistName", it.entity?.attributes?.curator)
//                    this.putString("album.url", it.entity?.attributes?.artwork?.urlWithDimensions)
//                    this.putBoolean("album.playlist", it.entity?.type.equals("library-playlists"))
//                    this.putString("album.description",it.entity?.attributes?.description?.standard)
//                    this.putString("transition_name", it.itemView.playlist_art.transitionName)
//                }
//                findNavController().navigate(R.id.show_details_for_playlist, args, null, extras)
            }
        }
    }

    override fun initView() {
        super.initView()

        root.artists.apply {
            val header = SpacesItemDecoration(10.dp, OrientationHelper.VERTICAL).apply {
                this.header = true
                this.topBottomOnly = true
            }
            val footer = SpacesItemDecoration(10.dp, OrientationHelper.VERTICAL).apply {
                this.bottomOnly = true
            }
            this.addItemDecorations(header, footer)
        }
        root.artists.adapter = artists

        enableToolbarTranslationEffects(false)
        showToolbarElevation(true)
        setToolbarTitle(R.string.title_artists)

        observe()
    }

    private fun observe() {
        vm?.artists?.observe(viewLifecycleOwner, Observer {
            artists.submitList(it)
        })
    }
}
