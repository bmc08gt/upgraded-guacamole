package dev.bmcreations.guacamole.auth

import android.content.Context
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.musicUserToken
import dev.bmcreations.guacamole.extensions.strings

class TokenProvider private constructor() {

    var devToken: String = ""
    var userToken: String? = null

    companion object Factory {
        var INSTANCE: TokenProvider? = null
        fun with(context: Context?): TokenProvider {
            val tmp = INSTANCE
            if (tmp != null) {
                if (tmp.userToken == null) {
                    tmp.userToken = musicUserToken(context)
                }
                return tmp
            }
            return synchronized(this) {
                TokenProvider().also { instance ->
                    INSTANCE = instance
                    context?.let { ctx ->
                        instance.devToken = ctx.strings[R.string.musickit_token]
                        instance.userToken = musicUserToken(ctx)
                    }
                }
            }
        }
    }
}