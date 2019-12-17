package dev.bmcreations.guacamole.auth

import android.accounts.AccountManager
import android.content.Context
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.*
import dev.bmcreations.guacamole.preferences.UserPreferences

class TokenProvider private constructor(private val context: Context, private val prefs: UserPreferences): com.apple.android.sdk.authentication.TokenProvider {

    override fun getDeveloperToken(): String = context.strings[R.string.musickit_token]

    override fun getUserToken(): String = prefs.currentUser?.token ?: ""

    companion object {
        fun with(context: Context, userPreferences: UserPreferences): TokenProvider {
            return TokenProvider(context, userPreferences)
        }
    }
}
