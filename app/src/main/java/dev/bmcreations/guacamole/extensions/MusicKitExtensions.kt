package dev.bmcreations.guacamole.extensions

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import dev.bmcreations.guacamole.ui.login.authy.GuacamoleAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val accountType: String = GuacamoleAuthenticator.ACCOUNT_TYPE
val authTokenType: String = GuacamoleAuthenticator.AUTH_TOKEN_TYPE

fun userSignedIn(context: Context?): Boolean {
    return context?.currentAccount != null
}

val Context?.currentAccount: Account?
get() {
    return this?.let {
        val accountManager = AccountManager.get(it)
        val accounts = accountManager.getAccountsByType(accountType)
        if (accounts.isEmpty()) {
            // do nothing
            return null
        }
        accounts[0]
    }
}

fun musicUserToken(context: Context?): String? {
    return context?.let { ctx ->
        ctx.currentAccount?.let { acct ->
            val am = AccountManager.get(context)
            val token = am.peekAuthToken(acct, authTokenType)
            if (token == null) {
                am.invalidateAuthToken(accountType, null)
            }
            am.peekAuthToken(acct, authTokenType)
        }
    }
}