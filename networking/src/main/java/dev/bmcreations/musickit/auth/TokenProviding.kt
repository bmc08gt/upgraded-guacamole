package dev.bmcreations.musickit.auth

interface TokenProviding {
    fun getDeveloperToken(): String
    fun getUserToken(): String
    fun onTokenExpired(reason: InvalidTokenReason)
}

sealed class InvalidTokenReason {
    object Unknown: InvalidTokenReason()
    object Unauthorized : InvalidTokenReason()
    object UnsecuredJWT : InvalidTokenReason()

    companion object {
        fun byCode(code: Int): InvalidTokenReason {
            return when (code) {
                401 -> Unauthorized
                403 -> UnsecuredJWT
                else -> Unknown
            }
        }
    }
}

fun Int.toTokenReason(): InvalidTokenReason =
    InvalidTokenReason.byCode(this)

