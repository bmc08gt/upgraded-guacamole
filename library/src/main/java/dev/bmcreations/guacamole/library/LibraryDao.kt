package dev.bmcreations.guacamole.library

import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track

interface LibraryDao {

    suspend fun getRecentlyAdded(): List<RecentlyAddedEntity>
    suspend fun getRecentlyAddedEntityById(id: String): RecentlyAddedEntity?
    suspend fun getPlaylists(): List<LibraryPlaylist>
    suspend fun getPlaylistById(id: String): LibraryPlaylist?
    suspend fun getAllSongs(): List<Track>
    suspend fun getSongById(id: String): Track?
    suspend fun getAllSongsForArtist(artistName: String): List<Track>

    suspend fun upsert(vararg recent: RecentlyAddedEntity)
    suspend fun upsert(vararg playlist: LibraryPlaylist)
    suspend fun upsert(vararg song: Track)

    suspend fun remove(recent: RecentlyAddedEntity)
    suspend fun remove(playlist: LibraryPlaylist)
    suspend fun remove(song: Track)

    suspend fun clear()
}
