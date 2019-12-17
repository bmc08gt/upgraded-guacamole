package dev.bmcreations.guacamole.graphs

import android.content.Context
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.musickit.networking.TokenExpiredCallback
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository

interface NetworkGraph {
    val musicRepository: MusicRepository
}

class NetworkGraphImpl(appContext: Context, expiryListener: TokenExpiredCallback): NetworkGraph {
    private val userPrefs = UserPreferences(appContext)
    private val tokenProvider = TokenProvider.with(appContext, userPrefs)
    override val musicRepository: MusicRepository = MusicRepository(tokenProvider, expiryListener)
}
