package dev.bmcreations.guacamole.ui

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
import org.jetbrains.anko.AnkoLogger

class HomeActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger {

    private var navController: NavController? = null

    private val session by lazy { graph().sessionGraph }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment).also {
            navView.setupWithNavController(it)
        }

        navController?.let { setupActionBarWithNavController(it) }
    }

    override fun onResume() {
        super.onResume()
        if(!session.sessionManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onSupportNavigateUp() = navController?.navigateUp() ?: false

    companion object {
        fun newIntent(caller: Context): Intent {
            return Intent(caller, HomeActivity::class.java)
        }
    }
}
