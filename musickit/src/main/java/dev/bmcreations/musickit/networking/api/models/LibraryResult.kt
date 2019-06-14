package dev.bmcreations.musickit.networking.api.models

sealed class LibraryResult
data class Playlist(val playlist: LibraryPlaylist): LibraryResult()
data class Album(val album: LibraryAlbum): LibraryResult()
