package dev.bmcreations.guacamole.repository

import android.content.Context
import android.content.Intent
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.models.User
import dev.bmcreations.guacamole.preferences.UserPreferences
import dev.bmcreations.guacamole.ui.MainActivity

class SessionManager(
    private val tokenProvider: TokenProvider,
    private val userPreferences: UserPreferences
) {
    var userToken: String = tokenProvider.userToken
    var devToken: String = tokenProvider.developerToken

    fun isLoggedIn(): Boolean = userPreferences.currentUser != null

    fun removeUser() {
        userPreferences.removeUser()
    }

    fun createUser(token: String) {
        userPreferences.currentUser = User(token)
    }
}
