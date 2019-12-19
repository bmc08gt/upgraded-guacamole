package dev.bmcreations.guacamole.preferences

import android.content.Context
import com.google.gson.Gson
import dev.bmcreations.guacamole.models.User

class UserPreferences(val context: Context) {

    companion object {
        const val PREFS = "user_prefs"

        const val CURRENT_USER = "current_user"
    }

    private val gson = Gson()

    private val sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private var _user: User? = null
    set(value) {
        field = value
        if (value == null) {
            sharedPreferences.edit().remove(CURRENT_USER).apply()
        } else {
            sharedPreferences.edit()
                .putString(CURRENT_USER, gson.toJson(value, User::class.java))
                .apply()
        }
    }
    var currentUser: User? = null
        get() {
            if (_user == null) {
                val json = sharedPreferences.getString(CURRENT_USER, null)
                if (json != null) {
                    _user = gson.fromJson(json, User::class.java)
                }
            }
            return _user
        }
        set(value) {
            _user = value
            field = value
        }

    fun removeUser() {
        currentUser = null
    }
}
