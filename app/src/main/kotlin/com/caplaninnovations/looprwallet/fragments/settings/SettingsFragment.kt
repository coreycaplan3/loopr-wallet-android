package com.caplaninnovations.looprwallet.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.SettingsActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Created by Corey on 3/23/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SettingsFragment : BaseSettingsFragment() {

    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName

        val PREFERENCE_KEY_CHANGE_THEME = ApplicationUtility.str(R.string.settings_theme_key)
        val PREFERENCE_KEY_SECURITY_SCREEN = ApplicationUtility.str(R.string.settings_security_screen_key)

        val DEFAULT_VALUE_THEME = ApplicationUtility.str(R.string.settings_theme_dark)
        val DEFAULT_VALUE_SECURITY_SCREEN = ApplicationUtility.str(R.string.summary_security_screen)
    }

    @Inject
    lateinit var themeSettings: ThemeSettings
    private val themeChangeKey = ApplicationUtility.str(R.string.settings_theme_key)

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        LooprWalletApp.dagger.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_main)
    }

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            Pair(PREFERENCE_KEY_CHANGE_THEME, DEFAULT_VALUE_THEME),
            Pair(PREFERENCE_KEY_SECURITY_SCREEN, DEFAULT_VALUE_SECURITY_SCREEN)
    )

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        super.onPreferenceChange(preference, newValue)

        val stringValue = newValue?.toString() ?: return false

        if (preference?.key == themeChangeKey &&
                themeSettings.getCurrentTheme() != themeSettings.getThemeFromSettings(stringValue)) {
            LooprSettings.getInstance(LooprWalletApp.context).putString(preference.key, stringValue)
            launch(UI) {
                // We are switching themes and need to recreate the back stack
                // We need to delay the launching so animations can finish
                delay(150L)

                activity?.let {
                    TaskStackBuilder.create(it)
                            .addNextIntent(Intent(it, MainActivity::class.java))
                            .addNextIntent(Intent(it, SettingsActivity::class.java))
                            .startActivities()
                }
            }
        }

        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val activity = (activity as? SettingsActivity) ?: return false
        return when (preference?.key) {
            PREFERENCE_KEY_SECURITY_SCREEN -> {
                activity.onNestedFragmentClick(SecuritySettingsFragment(), SecuritySettingsFragment.TAG)
                true
            }
            else -> false
        }
    }

}