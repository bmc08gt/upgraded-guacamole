package dev.bmcreations.guacamole.library

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.bmcreations.guacamole.models.apple.LibraryPlaylist
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.guacamole.models.apple.Track

@Database(
    entities = [RecentlyAddedEntity::class, LibraryPlaylist::class, Track::class],
    version = 1
)
@TypeConverters(dev.bmcreations.guacamole.converters.Converters::class)
abstract class RoomLibraryDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryContentsDao
}
