package dev.bmcreations.guacamole.graphs

import android.content.Context
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.musickit.networking.TokenExpiredCallback
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository

interface NetworkGraph {
    val musicRepository: MusicRepository
}

class NetworkGraphImpl(
    appContext: Context,
    prefs: UserPreferences? = null,
    tokenProvider: TokenProvider? = null,
    expiryListener: TokenExpiredCallback
) : NetworkGraph {
    private val tokens = if (tokenProvider == null) {
        val userPrefs = UserPreferences(appContext)
        TokenProvider.with(appContext, userPrefs)
    } else {
        tokenProvider
    }
    override val musicRepository: MusicRepository = MusicRepository(tokens, expiryListener)
}
