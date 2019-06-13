package dev.bmcreations.guacamole.ui.login

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apple.android.sdk.authentication.AuthenticationFactory
import com.apple.android.sdk.authentication.TokenError
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.extensions.authTokenType
import dev.bmcreations.guacamole.extensions.strings
import dev.bmcreations.guacamole.ui.login.authy.GuacamoleAuthenticator
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import dev.bmcreations.guacamole.viewmodel.LiveMessageEvent

class LoginViewModel private constructor(val context: Context): ViewModel() {

    companion object {
        const val MUSIC_KIT_AUTH = 1337

        fun create(context: Context): LoginViewModel {
            return LoginViewModel(context)
        }
    }

    sealed class State {
        data class Authenticated(val auth: String): State()
        object Unauthenticated: State()
        data class InvalidAuthentication(val cause: Throwable?): State()
    }

    val tokenProvider = TokenProvider.with(context)

    val authState = MutableLiveData<State>()

    private var authManager = AuthenticationFactory.createAuthenticationManager(context)

    val startActivityForResultEvent = LiveMessageEvent<ActivityNavigation>()

    init {
        refreshAuth()
    }

    fun refreshAuth() {
        authState.value = State.Unauthenticated
    }

    fun authenticate() {
        // TODO: Support webview callbacks for non-installed app
        startActivityForResultEvent.sendEvent {
            val intent = authManager.createIntentBuilder(tokenProvider.devToken)
                .setHideStartScreen(true)
                .setStartScreenMessage("Connect with Apple Music!")
                .build()
            this.startActivityForResult(intent, MUSIC_KIT_AUTH)
        }
    }

    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        val ret = authManager.handleTokenResult(data)
        if (ret.isError) {
            val error = when (ret.error) {
                TokenError.NO_SUBSCRIPTION -> Throwable("No subscription found for you")
                TokenError.TOKEN_FETCH_ERROR -> Throwable("Fetch error")
                TokenError.USER_CANCELLED -> Throwable("User cancelled auth request")
                TokenError.SUBSCRIPTION_EXPIRED -> Throwable("Your subscription is expired")
                else -> Throwable("¯\\_(ツ)_/¯")
            }
            authState.value = State.InvalidAuthentication(error)
        } else {
            createAccount(ret.musicUserToken).also {
                authState.value = State.Authenticated(ret.musicUserToken)
            }
        }
    }

    private fun createAccount(token: String) {
        val name = "Upgraded Guacamole"
        val authResult = Bundle().apply {
            this.putString(AccountManager.KEY_ACCOUNT_NAME, "name")
            this.putString(AccountManager.KEY_ACCOUNT_TYPE, GuacamoleAuthenticator.ACCOUNT_TYPE)
            this.putString(AccountManager.KEY_AUTHTOKEN, token)
        }

        AccountManager.get(context).also {
            val account = Account(name, GuacamoleAuthenticator.ACCOUNT_TYPE)
            it.addAccountExplicitly(account, null, authResult)
            it.setAuthToken(account, authTokenType, token)
        }
    }

}