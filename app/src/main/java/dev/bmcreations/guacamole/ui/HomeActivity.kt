package dev.bmcreations.guacamole.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.media.MediaStateLifecycleObserver
import dev.bmcreations.guacamole.ui.login.LoginActivity
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import dev.bmcreations.guacamole.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.AnkoLogger

class HomeActivity : AppCompatActivity(), ActivityNavigation, AnkoLogger, FragmentScrollChangeCallback {

    private var navController: NavController? = null

    private val session by lazy { graph().sessionGraph }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment).also {
            navView.setupWithNavController(it)
        }

        MediaStateLifecycleObserver.registerLifecycle(lifecycle)

        setSupportActionBar(mainToolbar)
        mainToolbar.registerAppBarLayout(appbar)

        navController?.let { setupActionBarWithNavController(it,
            AppBarConfiguration(topLevelDestinationIds = rootTabs))
        }
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

    override fun onScrollChange(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float {
        return mainToolbar.translate(scrollY, firstScroll, dragging)
    }

    override fun showElevation(show: Boolean) {
        mainToolbar.showElevation(show)
    }

    override fun setTitle(title: String?) {
        supportActionBar?.title = title
    }

    override fun enableScrollChange(enable: Boolean) {
        mainToolbar.enableTranslationEffect(enable)
    }

    companion object {
        fun newIntent(caller: Context): Intent {
            return Intent(caller, HomeActivity::class.java)
        }

        val rootTabs = setOf(R.id.menu_library, R.id.menu_for_you, R.id.menu_browse)
    }
}
