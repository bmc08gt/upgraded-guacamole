package dev.bmcreations.guacamole.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apple.android.sdk.authentication.AuthenticationFactory
import com.apple.android.sdk.authentication.TokenError
import dev.bmcreations.guacamole.repository.SessionManager
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import dev.bmcreations.guacamole.viewmodel.LiveMessageEvent

class LoginViewModel private constructor(
    context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableLiveData<State>()
    val authState: LiveData<State>
        get() = _authState

    private var authManager = AuthenticationFactory.createAuthenticationManager(context)

    val startActivityForResultEvent = LiveMessageEvent<ActivityNavigation>()

    init {
        refreshAuth()
    }

    fun refreshAuth() {
        _authState.value = State.Unauthenticated
    }

    fun authenticate() {
        // TODO: Support webview callbacks for non-installed app
        startActivityForResultEvent.sendEvent {
            val intent = authManager.createIntentBuilder(sessionManager.devToken)
                .setHideStartScreen(false)
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
            _authState.value = State.InvalidAuthentication(error)
        } else {
            sessionManager.createUser(ret.musicUserToken)
            _authState.value = State.Authenticated(ret.musicUserToken)
        }
    }

    companion object {
        const val MUSIC_KIT_AUTH = 1337

        fun create(context: Context, sessionManager: SessionManager): LoginViewModel {
            return LoginViewModel(context, sessionManager)
        }
    }

    sealed class State {
        data class Authenticated(val auth: String) : State()
        object Unauthenticated : State()
        data class InvalidAuthentication(val cause: Throwable?) : State()
    }
}
