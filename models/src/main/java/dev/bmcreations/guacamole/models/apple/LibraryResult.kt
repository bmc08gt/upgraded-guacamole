package dev.bmcreations.guacamole.models.apple

sealed class LibraryResult
data class Playlist(val playlist: LibraryPlaylist?): LibraryResult()
data class Album(val album: LibraryAlbum?): LibraryResult()
