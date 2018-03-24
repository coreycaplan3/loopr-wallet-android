package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.view.MenuItem
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.ifNull
import com.caplaninnovations.looprwallet.fragments.settings.SettingsFragment
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Purpose of class: To allow the user to interact with his/her configurable settings
 */
class SettingsActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_settings

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState.ifNull {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityContainer, SettingsFragment(), SettingsFragment.TAG)
                    .commit()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> false
    }
}