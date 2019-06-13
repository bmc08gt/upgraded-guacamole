package dev.bmcreations.guacamole.ui.library

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
import dev.bmcreations.guacamole.ui.library.recentlyadded.RecentlyAddedAdapter
import dev.bmcreations.guacamole.ui.widgets.SpacesItemDecoration
import dev.bmcreations.guacamole.ui.widgets.addItemDecorations
import kotlinx.android.synthetic.main.fragment_library.view.*
import kotlinx.android.synthetic.main.recently_added_entity.view.*
import org.jetbrains.anko.AnkoLogger

class LibraryFragment: Fragment(), AnkoLogger {

    val vm by lazy {
        context?.let { ctx -> LibraryViewModel(ctx) }
    }

    val recentlyAddedItems by lazy {
        RecentlyAddedAdapter().apply {
            this.onRecentClicked = {
                val extras = FragmentNavigatorExtras(it.itemView.ra_image to it.itemView.ra_image.transitionName)
                val args = Bundle().apply {
                    this.putString("album.id", it.entity?.id)
                    this.putString("album.name", it.entity?.attributes?.name)
                    this.putString("album.artistName", it.entity?.attributes?.artistName)
                    this.putString("album.url", it.entity?.attributes?.artwork?.urlWithDimensions)
                    this.putString("transition_name", it.itemView.ra_image.transitionName)
                }
                findNavController().navigate(R.id.show_details_for_album, args, null, extras)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_library, container, false)

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

        return root
    }

    override fun onResume() {
        super.onResume()
        vm?.refresh()
    }

    private fun observe() {
        vm?.recentlyAdded?.observe(this, Observer { recentlyAddedItems.submitList(it) })
    }
}