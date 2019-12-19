package dev.bmcreations.guacamole.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import dev.bmcreations.guacamole.BuildConfig
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.graph
import kotlinx.android.synthetic.main.activity_settings.view.*

class SettingsFragment: PreferenceFragmentCompat() {

    private val session by lazy { context?.graph()?.sessionGraph }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)


        preferenceScreen.apply {
            findPreference("key_version").apply {
                summary = BuildConfig.VERSION_NAME
            }

            findPreference("key_logout").apply {
                setOnPreferenceClickListener {
                    session?.sessionManager?.removeUser()
                    activity?.finish()
                    true
                }
            }
        }
    }
}
