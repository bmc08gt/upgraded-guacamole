package dev.bmcreations.guacamole.ui.navigation

import android.content.Intent

interface ActivityNavigation {
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}