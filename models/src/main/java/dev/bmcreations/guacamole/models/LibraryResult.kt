package dev.bmcreations.guacamole.models

sealed class LibraryResult
data class Playlist(val playlist: LibraryPlaylist?): LibraryResult()
data class Album(val album: dev.bmcreations.guacamole.models.LibraryAlbum?): LibraryResult()
