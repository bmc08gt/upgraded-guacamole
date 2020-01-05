package dev.bmcreations.guacamole.library

import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track

class Library(
    private val dao: LibraryDao
) {
    suspend fun recentlyAdded(): List<RecentlyAddedEntity> {
        return dao.getRecentlyAdded()
    }

    suspend fun addRecentlyAddedItems(items: List<RecentlyAddedEntity>) {
        dao.upsert(*items.toTypedArray())
    }

    suspend fun playlists(): List<LibraryPlaylist> {
        return dao.getPlaylists()
    }

    suspend fun addPlaylists(items: List<LibraryPlaylist>) {
        dao.upsert(*items.toTypedArray())
    }

    suspend fun songs(): List<Track> {
        return dao.getAllSongs()
    }

    suspend fun addSongs(items: List<Track>) {
        dao.upsert(*items.toTypedArray())
    }
}
