package dev.bmcreations.guacamole.ui

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger

class MainActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger {

    private val session by lazy { graph().sessionGraph.sessionManager }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        when {
            session.isLoggedIn() -> {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }
            !session.isLoggedIn() -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }
        finish()
    }

    companion object {
        fun newIntent(caller: Context): Intent = Intent(caller, MainActivity::class.java)
    }
}
