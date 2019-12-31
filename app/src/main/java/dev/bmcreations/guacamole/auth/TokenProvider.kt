package dev.bmcreations.guacamole.auth

import android.content.Context
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.networking.auth.InvalidTokenReason
import dev.bmcreations.networking.auth.TokenProviding

class TokenProvider private constructor(
    private val context: Context,
    private val prefs: UserPreferences,
    private val expiredCallback: TokenExpiredCallback? = null
): com.apple.android.sdk.authentication.TokenProvider,
    TokenProviding {

    override fun getDeveloperToken(): String = context.strings[R.string.musickit_token]

    override fun getUserToken(): String = prefs.currentUser?.token ?: ""

    companion object {
        fun with(context: Context, userPreferences: UserPreferences, expiredCallback: TokenExpiredCallback?): TokenProvider {
            return TokenProvider(context, userPreferences, expiredCallback)
        }
    }

    override fun onTokenExpired(reason: InvalidTokenReason) {
        expiredCallback?.onTokenExpired(reason)
    }
}

interface TokenExpiredCallback {
    fun onTokenExpired(reason: InvalidTokenReason)
}
