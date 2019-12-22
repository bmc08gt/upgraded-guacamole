package dev.bmcreations.guacamole.ui.library

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.OrientationHelper
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment
import dev.bmcreations.guacamole.ui.library.groupings.LibraryGrouping
import dev.bmcreations.guacamole.ui.library.groupings.LibraryGroupingAdapter
import dev.bmcreations.guacamole.ui.library.recentlyadded.RecentlyAddedAdapter
import dev.bmcreations.guacamole.ui.widgets.SpacesItemDecoration
import dev.bmcreations.guacamole.ui.widgets.addItemDecorations
import dev.bmcreations.musickit.networking.api.models.urlWithDimensions
import kotlinx.android.synthetic.main.fragment_library.view.*
import kotlinx.android.synthetic.main.recently_added_entity.view.*


class LibraryFragment: NavigationStackFragment() {

    private val librarySource get() = context?.graph()?.networkGraph?.librarySource
    private val musicQueue get() = context?.graph()?.sessionGraph?.musicQueue

    private val vm by lazy {
        librarySource?.let { source ->
            musicQueue?.let { queue ->
                getViewModel { LibraryViewModel(source, queue) } }
        }
    }

    private val libraryGroupings by lazy {
        LibraryGroupingAdapter().apply {
            this.onGroupingClicked = {
                when (it.entity) {
                    LibraryGrouping.Playlists -> {
                        findNavController().navigate(R.id.show_library_playlists)
                    }
                }

            }
        }
    }

    private val recentlyAddedItems by lazy {
        RecentlyAddedAdapter().apply {
            this.onRecentClicked = {
                val extras = FragmentNavigatorExtras(it.itemView.ra_image to it.itemView.ra_image.transitionName)
                val args = Bundle().apply {
                    this.putString("album.id", it.entity?.id)
                    this.putString("album.name", it.entity?.attributes?.name)
                    this.putString("album.artistName", it.entity?.attributes?.artistName)
                    this.putString("album.url", it.entity?.attributes?.artwork?.urlWithDimensions)
                    this.putBoolean("album.playlist", it.entity?.type.equals("library-playlists"))
                    this.putString("transition_name", it.itemView.ra_image.transitionName)
                }
                findNavController().navigate(R.id.show_details_for_album, args, null, extras)
            }
        }
    }

    override val layoutResId  get() = R.layout.fragment_library

    override fun initView() {
        root.groups.adapter = libraryGroupings
        // list is static so submit it now
        libraryGroupings.submitList(LibraryGrouping.values().toMutableList())

        root.recently_added.apply {
            val endcaps = SpacesItemDecoration(8.dp(context), OrientationHelper.HORIZONTAL).apply {
                this.endCaps = true
                this.grid = true
            }
            val horizontalSpacing = SpacesItemDecoration(4.dp(context), OrientationHelper.HORIZONTAL).apply {
                this.grid = true
            }
            val header = SpacesItemDecoration(10.dp(context), OrientationHelper.VERTICAL).apply {
                this.grid = true
                this.header = true
                this.topBottomOnly = true
            }
            val footer = SpacesItemDecoration(10.dp(context), OrientationHelper.VERTICAL).apply {
                this.bottomOnly = true
            }
            this.addItemDecorations(endcaps, horizontalSpacing, header, footer)
        }
        root.recently_added.adapter = recentlyAddedItems

        enableToolbarTranslationEffects(true)
        showToolbarElevation(false)

        root.scrollview.setScrollViewCallbacks(object : ObservableScrollViewCallbacks {
            override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) = Unit

            override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
                val dy = onScrollChange(scrollY, firstScroll, dragging)
                showToolbarElevation(show = (dy ?: 0f) >= 0)
            }

            override fun onDownMotionEvent() = Unit

        })

        observe()
    }

    private fun observe() {
        vm?.recentlyAdded?.observe(viewLifecycleOwner, Observer { recentlyAddedItems.submitList(it) })
        vm?.recentsNetworkState?.observe(viewLifecycleOwner, Observer { recentlyAddedItems.networkState = it })
    }
}
