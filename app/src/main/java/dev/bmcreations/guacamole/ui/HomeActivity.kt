package dev.bmcreations.guacamole.ui

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.ui.login.LoginActivity
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import dev.bmcreations.guacamole.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class HomeActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger, FragmentScrollChangeCallback {

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

        setSupportActionBar(mainToolbar)

        navController?.let { setupActionBarWithNavController(it) }
    }

    override fun onResume() {
        super.onResume()
        if(!session.sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settings -> {
                startActivity(SettingsActivity.newIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() = navController?.navigateUp() ?: false

    companion object {
        fun newIntent(caller: Context): Intent {
            return Intent(caller, HomeActivity::class.java)
        }
    }

    override fun onScrollChange(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float {
        return mainToolbar.translate(scrollY, firstScroll, dragging)
    }

    override fun showElevation(show: Boolean) {
        // If we have some pinned children, and we're offset to only show those views,
        // we want to be elevate
        info { "show=$show" }
        ViewCompat.setElevation(appbar, if (show) 8f else 0f)
    }

    override fun setTitle(title: String) {
        supportActionBar?.title = title
    }
}
