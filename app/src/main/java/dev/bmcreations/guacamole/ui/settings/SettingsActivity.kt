package dev.bmcreations.guacamole.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dev.bmcreations.guacamole.R
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger

class SettingsActivity: AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> finish().run { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newIntent(caller: Context): Intent {
            return Intent(caller, SettingsActivity::class.java)
        }
    }
}
