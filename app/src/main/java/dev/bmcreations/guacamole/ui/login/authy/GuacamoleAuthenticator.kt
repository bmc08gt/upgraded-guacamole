package dev.bmcreations.guacamole.ui.login.authy

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import dev.bmcreations.guacamole.ui.MainActivity

class GuacamoleAuthenticator internal constructor(private val context: Context): AbstractAccountAuthenticator(context) {

    companion object {
        const val ACCOUNT_TYPE = "guacamole_account_type"
        const val AUTH_TOKEN_TYPE = "guacamole_auth_token_type"
    }
    override fun getAuthTokenLabel(authTokenType: String?): String? = null

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle? = null

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? = null

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        val am = AccountManager.get(context)
        val token = am.peekAuthToken(account, authTokenType)

        return if (!TextUtils.isEmpty(token)) {
            Bundle().apply {
                this.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                this.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                this.putString(AccountManager.KEY_AUTHTOKEN, token)
            }
        } else {
            generateReAuthBundle(account.type, authTokenType, response)
        }
    }

    private fun generateReAuthBundle(accountType: String, authTokenType: String, response: AccountAuthenticatorResponse?): Bundle {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.putExtra(ACCOUNT_TYPE, accountType)
            this.putExtra(AUTH_TOKEN_TYPE, authTokenType)
            this.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        }
        return Bundle().apply {
            this.putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle? = null

    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? = null

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle = generateReAuthBundle(accountType, authTokenType, response)
}