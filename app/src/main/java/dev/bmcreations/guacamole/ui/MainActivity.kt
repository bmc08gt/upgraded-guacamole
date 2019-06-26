package dev.bmcreations.guacamole.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.userSignedIn
import dev.bmcreations.guacamole.ui.login.LoginActivity
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import org.jetbrains.anko.AnkoLogger

class MainActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger {

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
        if (!userSignedIn(this)) {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onSupportNavigateUp() = navController?.navigateUp() ?: false
}