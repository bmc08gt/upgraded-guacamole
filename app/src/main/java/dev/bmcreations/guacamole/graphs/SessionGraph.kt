package dev.bmcreations.guacamole.graphs

import android.content.Context
import com.apple.android.music.playback.controller.MediaPlayerController
import com.apple.android.music.playback.controller.MediaPlayerControllerFactory
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.guacamole.repository.SessionManager
import dev.bmcreations.musickit.networking.TokenExpiredCallback
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository

interface SessionGraph {
    val tokenProvider: TokenProvider
    val sessionManager: SessionManager
    val userPreferences: UserPreferences
}

class SessionGraphImpl(appContext: Context): SessionGraph {
    override val userPreferences: UserPreferences = UserPreferences(appContext)
    override val tokenProvider: TokenProvider = TokenProvider.with(appContext, userPreferences)
    override val sessionManager: SessionManager = SessionManager(tokenProvider, userPreferences)
}
