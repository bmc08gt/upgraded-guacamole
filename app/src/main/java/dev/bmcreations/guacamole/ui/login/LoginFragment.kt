package dev.bmcreations.guacamole.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.ui.navigation.ActivityNavigation
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment(), ActivityNavigation {

    private val loginVm by lazy {
        activity?.let { a -> getViewModel { LoginViewModel.create(a) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            loginVm?.startActivityForResultEvent?.setEventReceiver(it, this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInButton.setOnClickListener { signIn() }
        closeButton.setOnClickListener { activity?.finish() }
    }

    private fun signIn() {
        loginVm?.authenticate()
    }
}