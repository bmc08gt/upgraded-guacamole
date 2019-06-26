package dev.bmcreations.guacamole.ui.library.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.OrientationHelper
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.ui.library.LibraryViewModel
import dev.bmcreations.guacamole.ui.widgets.SpacesItemDecoration
import dev.bmcreations.guacamole.ui.widgets.addItemDecorations
import dev.bmcreations.musickit.networking.api.models.curator
import dev.bmcreations.musickit.networking.api.models.urlWithDimensions
import kotlinx.android.synthetic.main.fragment_library_playlists.view.*
import kotlinx.android.synthetic.main.library_playlist_row_entity.view.*
import kotlinx.android.synthetic.main.recently_added_entity.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class LibraryPlaylistFragment: Fragment(), AnkoLogger {

    val vm by lazy {
        activity?.let { ctx -> LibraryViewModel(ctx) }
    }

    val playlists by lazy {
        LibraryPlaylistAdapter().apply {
            onPlaylistClicked = {
                val extras = FragmentNavigatorExtras(it.itemView.playlist_art to it.itemView.playlist_art.transitionName)
                val args = Bundle().apply {
                    this.putString("album.id", it.entity?.id)
                    this.putString("album.name", it.entity?.attributes?.name)
                    this.putString("album.artistName", it.entity?.attributes?.curator)
                    this.putString("album.url", it.entity?.attributes?.artwork?.urlWithDimensions)
                    this.putBoolean("album.playlist", it.entity?.type.equals("library-playlists"))
                    this.putString("album.description",it.entity?.attributes?.description?.standard)
                    this.putString("transition_name", it.itemView.playlist_art.transitionName)
                }
                findNavController().navigate(R.id.show_details_for_playlist, args, null, extras)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_library_playlists, container, false)
        root.playlists.apply {
            val header = SpacesItemDecoration(10.dp(context), OrientationHelper.VERTICAL).apply {
                this.header = true
                this.topBottomOnly = true
            }
            val footer = SpacesItemDecoration(10.dp(context), OrientationHelper.VERTICAL).apply {
                this.bottomOnly = true
            }
            this.addItemDecorations(header, footer)
        }
        root.playlists.adapter = playlists
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observe()
    }

    private fun observe() {
        vm?.playlists?.observe(viewLifecycleOwner, Observer { playlists.submitList(it) })
    }
}