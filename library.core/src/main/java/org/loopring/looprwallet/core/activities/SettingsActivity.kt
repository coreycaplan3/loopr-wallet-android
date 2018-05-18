package org.loopring.looprwallet.core.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.appbar_main.*
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.ifNull
import org.loopring.looprwallet.core.fragments.security.BaseSecurityFragment
import org.loopring.looprwallet.core.fragments.security.OnSecurityChangeListener
import org.loopring.looprwallet.core.fragments.settings.BaseSettingsFragment
import org.loopring.looprwallet.core.fragments.settings.HomeSettingsFragment
import org.loopring.looprwallet.core.fragments.settings.SecuritySettingsFragment
import org.loopring.looprwallet.core.models.settings.ThemeSettings
import org.loopring.looprwallet.core.utilities.ViewUtility
import javax.inject.Inject

/**
 * Purpose of class: To allow the user to interact with his/her configurable settings
 */
class SettingsActivity : AppCompatActivity(), OnSecurityChangeListener {

    companion object {

        const val KEY_FINISH_ALL = "_FINISH_ALL"

        /**
         * @return An intent used to kill the entire application, if started
         */
        fun createIntentToFinishApp(): Intent {
            return Intent(CoreLooprWalletApp.application.applicationContext, SettingsActivity::class.java)
                    .putExtra(KEY_FINISH_ALL, true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        fun route(fragment: Fragment) {
            fragment.startActivity(Intent(CoreLooprWalletApp.application, SettingsActivity::class.java))
        }

    }

    @Inject
    lateinit var themeSettings: ThemeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreLooprComponent.inject(this)

        // We MUST set the theme before the call to setContentView
        setTheme(themeSettings.getCurrentTheme())

        setContentView(R.layout.activity_settings)

        savedInstanceState.ifNull {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityContainer, HomeSettingsFragment(), HomeSettingsFragment.TAG)
                    .commit()
        }

        setSupportActionBar(toolbar)
        toolbar?.navigationIcon = ViewUtility.getNavigationIcon(R.drawable.ic_arrow_back_white_24dp, theme)
        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        isIntentForClosingApplication()
    }

    /**
     * Called when a nested fragment needs to be added to the fragment stack.
     *
     * @param fragment The nested fragment to be pushed to the front
     * @param tag The tag to associate with the fragment and its position in the back-stack.
     */
    fun onNestedFragmentClick(fragment: BaseSettingsFragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_right_to_center, R.anim.slide_center_to_left, R.anim.slide_left_to_center, R.anim.slide_center_to_right)
                .replace(R.id.activityContainer, fragment, tag)
                .addToBackStack(tag)
                .commit()
    }

    /**
     * Called when a security fragment needs to be pushed onto the fragment stack
     *
     * @param fragment The security fragment to be pushed to the front
     * @param tag The tag to associate with the fragment and its position in the back-stack.
     */
    fun onSecuritySettingsFragmentClick(fragment: BaseSecurityFragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_down)
                .replace(R.id.activityContainer, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss()
    }

    override fun onSecurityEnabled(securityType: String) {
        val fragment = supportFragmentManager.findFragmentByTag(SecuritySettingsFragment.TAG)
        (fragment as? SecuritySettingsFragment)?.onSecurityEnabled(securityType)

        supportFragmentManager.popBackStack()
    }

    override fun onSecurityDisabled() {
        val fragment = supportFragmentManager.findFragmentByTag(SecuritySettingsFragment.TAG)
        (fragment as? SecuritySettingsFragment)?.onSecurityDisabled()

        supportFragmentManager.popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // MARK - Private Methods

    /**
     * @return True if the intent from [getIntent] was sent to close the application (and the
     * application will close) or false otherwise.
     */
    private fun isIntentForClosingApplication(): Boolean {
        return if (intent.getBooleanExtra(KEY_FINISH_ALL, false)) {
            // The user requested to exit the app
            finish()
            true
        } else {
            false
        }
    }

}