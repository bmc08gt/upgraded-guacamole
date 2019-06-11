package dev.bmcreations.guacamole.extensions

import android.accounts.AccountManager
import android.content.Context
import dev.bmcreations.guacamole.ui.login.authy.GuacamoleAuthenticator


fun userSignedIn(context: Context): Boolean {
    return AccountManager.get(context).getAccountsByType(GuacamoleAuthenticator.ACCOUNT_TYPE).isNotEmpty()
}