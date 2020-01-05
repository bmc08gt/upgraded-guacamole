package dev.bmcreations.guacamole.graphs

import android.content.Context
import androidx.room.Room
import dev.bmcreations.guacamole.auth.TokenExpiredCallback
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.library.InMemoryLibraryDao
import dev.bmcreations.guacamole.library.Library
import dev.bmcreations.guacamole.library.RoomLibraryDao
import dev.bmcreations.guacamole.library.RoomLibraryDatabase
import dev.bmcreations.guacamole.media.MediaState
import dev.bmcreations.guacamole.media.MusicQueue
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.guacamole.repository.SessionManager

interface SessionGraph {
    val tokenProvider: TokenProvider
    val sessionManager: SessionManager
    val userPreferences: UserPreferences
    val mediaState: MediaState
    val library: Library
}

class SessionGraphImpl(appContext: Context, expiredCallback: TokenExpiredCallback? = null) :
    SessionGraph {
    override val userPreferences: UserPreferences = UserPreferences(appContext)
    override val tokenProvider: TokenProvider =
        TokenProvider.with(appContext, userPreferences, expiredCallback)
    override val sessionManager: SessionManager = SessionManager(tokenProvider, userPreferences)
    override val mediaState: MediaState = MediaState(appContext, MusicQueue())

    private enum class DatabaseType { IN_MEMORY, ROOM }

    private val dbType = DatabaseType.ROOM

    private val libraryDao = when (dbType) {
        DatabaseType.IN_MEMORY -> InMemoryLibraryDao()
        DatabaseType.ROOM -> RoomLibraryDao(
            Room.databaseBuilder(
                appContext,
                RoomLibraryDatabase::class.java,
                "library_room.db"
            ).build()
        )

    }
    override val library: Library = Library(libraryDao)
}
