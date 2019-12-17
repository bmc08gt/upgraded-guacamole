package dev.bmcreations.guacamole.repository

import com.apple.android.sdk.authentication.TokenProvider
import dev.bmcreations.guacamole.models.User
import dev.bmcreations.guacamole.preferences.UserPreferences

class SessionManager(
    private val tokenProvider: TokenProvider,
    private val userPreferences: UserPreferences
) {
    var userToken: String = tokenProvider.userToken
    var devToken: String = tokenProvider.developerToken

    val isLoggedIn: Boolean
        get() = userPreferences.currentUser != null

    fun removeUser() {
        userPreferences.removeUser()
    }

    fun createUser(token: String) {
        userPreferences.currentUser = User(token)
    }
}
