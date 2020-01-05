package dev.bmcreations.guacamole.library

import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track

class RoomLibraryDao(private val database: RoomLibraryDatabase) : LibraryDao {
    override suspend fun getRecentlyAdded(): List<RecentlyAddedEntity> {
        return database.libraryDao().selectAllRecents().sortedBy { it._id }
    }

    override suspend fun getRecentlyAddedEntityById(id: String): RecentlyAddedEntity? {
        return database.libraryDao().getRecentById(id)
    }

    override suspend fun getPlaylists(): List<LibraryPlaylist> {
        return database.libraryDao().selectAllPlaylists()
    }

    override suspend fun getPlaylistById(id: String): LibraryPlaylist? {
        return database.libraryDao().getPlaylistById(id)
    }

    override suspend fun getAllSongs(): List<Track> {
        return database.libraryDao().selectAllSongs().sortedBy { it.attributes?.name?.toLowerCase() }
    }

    override suspend fun getSongById(id: String): Track? {
        return database.libraryDao().getSongById(id)
    }

    override suspend fun getAllSongsForArtist(artistName: String): List<Track> {
        return database.libraryDao().getSongsByArtist(artistName)
    }

    override suspend fun upsert(vararg recent: RecentlyAddedEntity) {
        database.libraryDao().upsert(*recent)
    }

    override suspend fun upsert(vararg playlist: LibraryPlaylist) {
        database.libraryDao().upsert(*playlist)
    }

    override suspend fun upsert(vararg song: Track) {
        database.libraryDao().upsert(*song)
    }

    override suspend fun remove(recent: RecentlyAddedEntity) {
        database.libraryDao().removeRecentlyAddedItem(recent)
    }

    override suspend fun remove(playlist: LibraryPlaylist) {
        database.libraryDao().removePlaylist(playlist)
    }

    override suspend fun remove(song: Track) {
        database.libraryDao().removeSong(song)
    }

    override suspend fun clear() {
        database.libraryDao().emptyRecents()
        database.libraryDao().emptyPlaylists()
        database.libraryDao().emptySongs()
    }
}
