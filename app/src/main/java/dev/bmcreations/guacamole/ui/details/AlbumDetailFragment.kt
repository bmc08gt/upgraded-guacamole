package dev.bmcreations.guacamole.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.OrientationHelper
import androidx.transition.TransitionInflater
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.ui.library.LibraryViewModel
import dev.bmcreations.guacamole.ui.playback.NowPlayingViewModel
import dev.bmcreations.musickit.networking.api.models.*
import kotlinx.android.synthetic.main.fragment_album_detail.*
import kotlinx.android.synthetic.main.fragment_album_detail.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AlbumDetailFragment : Fragment(), AnkoLogger {

    val vm by lazy {
        context?.let { ctx -> LibraryViewModel(ctx) }
    }

    val nowPlaying by lazy {
        activity?.let { a -> a.getViewModel { NowPlayingViewModel.create(a) } }
    }

    val adapter by lazy {
        TrackListAdapter().apply {
            this.onTrackSelected = {
                nowPlaying?.updatePlayingTrack(it.entity)
            }
        }
    }

    private var playlist: Boolean = false
    private var albumName: String? = null
    private var albumArtist: String? = null
    private var albumId: String? = null
    private var albumUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumName = arguments?.getString("album.name")
        albumArtist = arguments?.getString("album.artistName")
        albumId = arguments?.getString("album.id")
        albumUrl = arguments?.getString("album.url")
        playlist = arguments?.getBoolean("album.playlist") ?: false
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_album_detail, container, false)
        ViewCompat.setTransitionName(root.ra_image, arguments?.getString("transition_name", null))

        root.tracks.addItemDecoration(DividerItemDecoration(context, OrientationHelper.VERTICAL))
        root.tracks.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.fragment_library, false)
        }
        loadAlbumArt(albumUrl)

        albumArtist?.let { artist_name.text = it }
        albumName?.let { album_name.text = it }
        albumId?.let {
            if (playlist) {
                vm?.getLibraryPlaylistWithTracksById(it)
            } else {
                vm?.getLibraryAlbumById(it)
            }
        }
        observe()
    }

    private fun loadAlbumArt(url: String?) {
        picasso {
            it.cancelRequest(ra_image)
            it.load(url).error(R.drawable.ic_music_fail).resize(200, 200).into(ra_image)
        }
    }

    private fun observe() {
        vm?.selected?.observe(this, Observer {
            it?.let { ret ->
                when (ret) {
                    is Playlist -> loadPlaylist(ret.playlist)
                    is Album -> loadAlbum(ret.album)
                }
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
        info { "track three=${album.relationships?.tracks?.data?.find { d -> d?.attributes?.trackNumber == 3 }?.attributes?.durationInMillis}" }
        album.relationships?.tracks?.data?.let {
            adapter.submitList(it.filterNotNull().map { t -> AlbumTrackEntity(t) })
        }
    }

    private fun loadPlaylist(container: LibraryPlaylist) {
        val album = container
        if (ra_image.drawable == null) {
            val url = albumUrl ?: album.attributes?.artwork?.urlWithDimensions
            loadAlbumArt(url)
        }
        album_name.text = album.attributes?.name
        artist_name.invisible()
        context?.let { ctx ->
            album_duration.text = ctx.formattedStrings[R.string.album_length](
                album.tracks.lastIndex.toString(),
                album.durationInMinutes.toString()
            )
        }

        adapter.submitList(album.tracks.map { t -> PlaylistTrackEntity(t) })
    }
}