package dev.bmcreations.guacamole.graphs

import android.content.Context
import dev.bmcreations.guacamole.auth.TokenExpiredCallback
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.guacamole.repository.SessionManager
import dev.bmcreations.guacamole.media.MusicQueue

interface SessionGraph {
    val tokenProvider: TokenProvider
    val sessionManager: SessionManager
    val userPreferences: UserPreferences
    val musicQueue: MusicQueue
}

class SessionGraphImpl(appContext: Context, expiredCallback: TokenExpiredCallback? = null): SessionGraph {
    override val userPreferences: UserPreferences = UserPreferences(appContext)
    override val tokenProvider: TokenProvider = TokenProvider.with(appContext, userPreferences, expiredCallback)
    override val sessionManager: SessionManager = SessionManager(tokenProvider, userPreferences)
    override val musicQueue: MusicQueue =
        MusicQueue()
}
