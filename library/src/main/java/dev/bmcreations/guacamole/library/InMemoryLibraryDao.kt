package dev.bmcreations.guacamole.library

import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track
import java.lang.IllegalArgumentException

class InMemoryLibraryDao : LibraryDao {

    private val recentlyAdded: MutableMap<String, RecentlyAddedEntity> = mutableMapOf()
    private val playlists: MutableMap<String, LibraryPlaylist> = mutableMapOf()
    private val songs: MutableMap<String, Track> = mutableMapOf()

    override suspend fun getRecentlyAdded(): List<RecentlyAddedEntity> {
        return recentlyAdded.values.toList().sortedBy { it._id }
    }

    override suspend fun getRecentlyAddedEntityById(id: String): RecentlyAddedEntity? {
        return recentlyAdded[id]
    }

    override suspend fun getPlaylists(): List<LibraryPlaylist> {
        return playlists.values.toList()
    }

    override suspend fun getPlaylistById(id: String): LibraryPlaylist? {
        return playlists[id]
    }

    override suspend fun getAllSongs(): List<Track> {
        return songs.values.toList().sortedBy { it.attributes?.name?.toLowerCase() }
    }

    override suspend fun getSongById(id: String): Track? {
        return songs[id]
    }

    override suspend fun getAllSongsForArtist(artistName: String): List<Track> {
        return songs.values.filter { it.attributes?.artistName == artistName }
            .sortedBy { it.attributes?.name?.toLowerCase() }
    }

    override suspend fun upsert(vararg recent: RecentlyAddedEntity) {
        val newItems = recent.mapNotNull {
            if (it.id != null) {
                Pair(it.id!!, it)
            } else {
                null
            }
        }.toTypedArray()
        recentlyAdded.putAll(pairs = newItems)
    }

    override suspend fun upsert(vararg playlist: LibraryPlaylist) {
        val newItems = playlist.mapNotNull {
            if (it.id != null) {
                Pair(it.id!!, it)
            } else {
                null
            }
        }.toTypedArray()
        this.playlists.putAll(pairs = newItems)
    }

    override suspend fun upsert(vararg song: Track) {
        val newItems = song.mapNotNull {
            if (it.id != null) {
                Pair(it.id!!, it)
            } else {
                null
            }
        }.toTypedArray()
        this.songs.putAll(pairs = newItems)
    }

    override suspend fun remove(recent: RecentlyAddedEntity) {
        recentlyAdded.remove(recent.id)
    }

    override suspend fun remove(playlist: LibraryPlaylist) {
        playlists.remove(playlist.id)
    }

    override suspend fun remove(song: Track) {
        songs.remove(song.id)
    }

    override suspend fun clear() {
        recentlyAdded.clear()
        playlists.clear()
        songs.clear()
    }
}

fun safelyGetId(predicate: () -> String?, receiver: (String) -> Unit) {
    try {
        require(predicate.invoke() != null) { "ID is null for this element" }
        predicate.invoke()?.let(receiver)
    } catch (e: IllegalArgumentException) {
    }
}
