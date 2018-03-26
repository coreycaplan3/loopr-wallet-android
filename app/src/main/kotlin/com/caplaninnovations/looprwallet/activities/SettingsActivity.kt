package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.ifNull
import com.caplaninnovations.looprwallet.fragments.settings.BaseSettingsFragment
import com.caplaninnovations.looprwallet.fragments.settings.HomeSettingsFragment
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.utilities.ViewUtility
import kotlinx.android.synthetic.main.appbar_main.*
import javax.inject.Inject

/**
 * Purpose of class: To allow the user to interact with his/her configurable settings
 */
class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var themeSettings: ThemeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LooprWalletApp.dagger.inject(this)

        // We MUST set the theme before the call to setContentView
        setTheme(themeSettings.getCurrentTheme())

        setContentView(R.layout.activity_settings)

        savedInstanceState.ifNull {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityContainer, HomeSettingsFragment(), HomeSettingsFragment.TAG)
                    .commit()
        }

        setSupportActionBar(toolbar)
        toolbar?.navigationIcon = ViewUtility.getNavigationIcon(theme)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Called when a nested fragment needs to be added to the fragment stack.
     *
     * @param fragment The nested fragment to be pushed to the front
     * @param tag The tag to associate with the fragment and its position in the backstack.
     */
    fun onNestedFragmentClick(fragment: BaseSettingsFragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_right_to_center, R.anim.slide_center_to_left, R.anim.slide_left_to_center, R.anim.slide_center_to_right)
                .replace(R.id.activityContainer, fragment, tag)
                .addToBackStack(tag)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}