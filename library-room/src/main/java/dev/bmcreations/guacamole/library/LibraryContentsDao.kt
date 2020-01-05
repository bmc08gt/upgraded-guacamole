package dev.bmcreations.guacamole.library

import androidx.room.*
import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track

@Dao
interface LibraryContentsDao {

    @Query("SELECT * FROM RecentlyAddedEntity")
    fun selectAllRecents(): List<RecentlyAddedEntity>

    @Query("SELECT * FROM LibraryPlaylist")
    fun selectAllPlaylists(): List<LibraryPlaylist>

    @Query("SELECT * FROM Track")
    fun selectAllSongs(): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(vararg recent: RecentlyAddedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(vararg playlist: LibraryPlaylist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(vararg song: Track)

    @Query("SELECT * FROM RecentlyAddedEntity WHERE id = :id LIMIT 1")
    fun getRecentById(id: String): RecentlyAddedEntity?

    @Query("SELECT * FROM LibraryPlaylist WHERE id = :id LIMIT 1")
    fun getPlaylistById(id: String): LibraryPlaylist?

    @Query("SELECT * FROM Track WHERE id = :id LIMIT 1")
    fun getSongById(id: String): Track?

    @Query("SELECT * FROM Track WHERE attr_artistName = :artistName ")
    fun getSongsByArtist(artistName: String): List<Track>

    @Delete
    fun removeRecentlyAddedItem(recent: RecentlyAddedEntity)

    @Delete
    fun removePlaylist(playlist: LibraryPlaylist)

    @Delete
    fun removeSong(song: Track)

    @Query("DELETE FROM RecentlyAddedEntity")
    fun emptyRecents()

    @Query("DELETE FROM LibraryPlaylist")
    fun emptyPlaylists()

    @Query("DELETE FROM Track")
    fun emptySongs()

}
