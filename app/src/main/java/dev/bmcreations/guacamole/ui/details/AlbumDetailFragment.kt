package dev.bmcreations.guacamole.ui.details

import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.OrientationHelper
import androidx.transition.TransitionInflater
import coil.api.load
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.media.State
import dev.bmcreations.guacamole.models.apple.*
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment
import dev.bmcreations.guacamole.ui.library.LibraryViewModel
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel
import kotlinx.android.synthetic.main.fragment_album_detail.*
import kotlinx.android.synthetic.main.fragment_album_detail.view.*

class AlbumDetailFragment : NavigationStackFragment() {

    private val library get() = context?.graph()?.sessionGraph?.library
    private val librarySource get() = context?.graph()?.networkGraph?.librarySource
    private val geniusSearch get() = context?.graph()?.networkGraph?.geniusSearchSource
    private val mediaState get() = context?.graph()?.sessionGraph?.mediaState

    private val vm by lazy {
        library?.let { lib ->
            librarySource?.let { source ->
                geniusSearch?.let { genius ->
                    getViewModel { LibraryViewModel(lib, source, genius) }
                }
            }
        }
    }

    private val nowPlaying by lazy {
        activity?.let { a ->
            mediaState?.let {
                a.getViewModel { NowPlayingViewModel.create(it) }
            }
        }
    }

    private val adapter by lazy {
        TrackListAdapter().apply {
            nowPlaying = this@AlbumDetailFragment.nowPlaying
            onTrackSelected = {
                nowPlaying?.play(it.entity)
            }
        }
    }

    private var collection: Container? = null
    private var playlist: Boolean = false
    private var descriptionSummary: String? = null
    private var albumName: String? = null
    private var albumArtist: String? = null
    private var albumId: String? = null
    private var albumUrl: String? = null

    override val layoutResId get() = R.layout.fragment_album_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumName = arguments?.getString("album.name")
        albumArtist = arguments?.getString("album.artistName")
        albumId = arguments?.getString("album.id")
        albumUrl = arguments?.getString("album.url")
        playlist = arguments?.getBoolean("album.playlist") ?: false
        descriptionSummary = arguments?.getString("album.description")
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun initView() {
        ViewCompat.setTransitionName(root.ra_image, arguments?.getString("transition_name", null))

        root.tracks.addItemDecoration(DividerItemDecoration(context, OrientationHelper.VERTICAL))
        root.tracks.adapter = adapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.menu_library, false)
        }
        loadAlbumArt(albumUrl)

        albumArtist?.let { artist_name.text = it }
        albumName?.let { album_name.text = it }
        albumId?.let { id ->
            if (playlist) {
                vm?.getLibraryPlaylistWithTracksById(id) { result -> result.playlist?.let { loadPlaylist(it) } }
            } else {
                vm?.getLibraryAlbumById(id) { result -> result.album?.let { loadAlbum(it) } }
            }
        }
        descriptionSummary?.let {
            description.text = it
            description_group.visible()
        }

        play_collection.setOnClickListener { nowPlaying?.playCollection(collection) }
        shuffle_collection.setOnClickListener { nowPlaying?.shuffleCollection(collection) }

        enableToolbarTranslationEffects(true)
        setToolbarTitle(albumName)
        showToolbarElevation(false)

        root.root.setScrollViewCallbacks(object : ObservableScrollViewCallbacks {
            override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) = Unit

            override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
                val dy = onScrollChange(scrollY, firstScroll, dragging)
                showToolbarElevation(show = (dy ?: 0f) >= 0)
            }

            override fun onDownMotionEvent() = Unit

        })

        observe()
    }

    private fun loadAlbumArt(url: String?) {
        ra_image.load(url) {
            crossfade(true)
            error(R.drawable.ic_music_fail)
            size(600, 600)
        }
    }

    private fun observe() {
        mediaState?.playState?.observe(viewLifecycleOwner, Observer {
            when (it) {
                State.Playing,
                State.Paused -> tracks.swapAdapter(adapter, true)
            }
        })
    }

    private fun loadAlbum(container: LibraryAlbum) {
        val album = container
        if (ra_image.drawable == null) {
            val url = albumUrl ?: album.attributes?.artwork?.urlWithDimensions
            loadAlbumArt(url)
        }
        album_name.text = album.attributes?.name
        artist_name.text = album.attributes?.artistName
        artist_name.visible()
        album.attributes?.trackCount?.let { tracks ->
            album.relationships?.tracks?.durationInMinutes?.let { length ->
                context?.let { ctx ->
                    album_duration.text =
                        ctx.formattedStrings[R.string.album_length](tracks.toString(), length.toString())
                }
            }
        }
        collection = album
        adapter.submitList(album.toEntities())
    }

    private fun loadPlaylist(container: LibraryPlaylist) {
        if (ra_image.drawable == null) {
            val url = albumUrl ?: container.attributes?.artwork?.urlWithDimensions
            loadAlbumArt(url)
        }
        album_name.text = container.attributes?.name
        if (artist_name.text.isEmpty()) {
            if (container.attributes?.curator == null) {
                artist_name.invisible()
            } else {
                artist_name.text = container.attributes?.curator
            }
        }
        context?.let { ctx ->
            album_duration.text = ctx.formattedStrings[R.string.album_length](
                container.trackList?.lastIndex.toString(),
                container.durationInMinutes.toString()
            )
        }
        collection = container
        adapter.submitList(container.toEntities())
    }
}
