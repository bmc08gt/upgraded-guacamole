package dev.bmcreations.guacamole.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.ui.HomeActivity
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class LoginActivity: AppCompatActivity(), AnkoLogger, ActivityNavigation {

    private val sessionGraph by lazy { graph().sessionGraph }
    private val sessionManager by lazy { sessionGraph.sessionManager }

    private val loginVm by lazy {
        getViewModel { LoginViewModel.create(this, sessionManager) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val finalHost = NavHostFragment.create(R.navigation.login_navigation)
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_nav_host_fragment, finalHost)
            .commit()

        loginVm.startActivityForResultEvent.setEventReceiver(this, this)

        observe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginVm.onResultFromActivity(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun observe() {
        loginVm.authState.observe(this, Observer {
            when (it) {
                is LoginViewModel.State.Authenticated -> {
                    info { "Authenticated successfully" }
                    finish()
                    startActivity(HomeActivity.newIntent(this))
                }
                is LoginViewModel.State.Unauthenticated -> info { "Not authenticated" }
                is LoginViewModel.State.InvalidAuthentication -> {
                    info { "Invalid authentication -- ${it.cause?.localizedMessage}" }
                    toast("${it.cause?.localizedMessage}")
                }
            }
        })
    }
}
