package dev.bmcreations.guacamole.auth

import android.content.Context
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.musicUserToken
import dev.bmcreations.guacamole.extensions.strings

class TokenProvider private constructor(): com.apple.android.sdk.authentication.TokenProvider {
    override fun getDeveloperToken(): String {
        return devToken
    }

    override fun getUserToken(): String {
        return _userToken
    }

    private var devToken: String = ""
    private var _userToken: String = ""

    companion object Factory {
        private var INSTANCE: TokenProvider? = null
        fun with(context: Context?): TokenProvider {
            val tmp = INSTANCE
            if (tmp != null) {
                if (tmp._userToken.isEmpty()) {
                    tmp._userToken = musicUserToken(context) ?: ""
                }
                return tmp
            }
            return synchronized(this) {
                TokenProvider().also { instance ->
                    INSTANCE = instance
                    context?.let { ctx ->
                        instance.devToken = ctx.strings[R.string.musickit_token]
                        instance._userToken = musicUserToken(ctx) ?: ""
                    }
                }
            }
        }
    }
}