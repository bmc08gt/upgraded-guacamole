package dev.bmcreations.guacamole.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.ui.login.LoginActivity
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import org.jetbrains.anko.AnkoLogger

class MainActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger {

    private val session by lazy { graph().sessionGraph.sessionManager }

    private var authProvided = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            session.isLoggedIn || authProvided -> {
                finish()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            !session.isLoggedIn -> {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}
