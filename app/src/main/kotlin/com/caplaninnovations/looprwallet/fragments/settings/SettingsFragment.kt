package com.caplaninnovations.looprwallet.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceChangeListener
import android.support.v7.preference.PreferenceFragmentCompat
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
 *
 *
 */
class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceChangeListener {

    companion object {

        val TAG: String = SettingsFragment::class.java.simpleName

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see onPreferenceChangeListener
         */
        fun bindPreferenceSummaryToValue(
                preference: Preference,
                onPreferenceChangeListener: OnPreferenceChangeListener
        ) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = onPreferenceChangeListener

            // Trigger the listener immediately with the preference's current value.
            val settings = LooprSettings.getInstance(LooprWalletApp.context)
            val value = settings.getString(preference.key) ?: ""
            onPreferenceChangeListener.onPreferenceChange(preference, value)
        }

    }

    @Inject
    lateinit var themeSettings: ThemeSettings
    private val themeChangeKey = ApplicationUtility.str(R.string.settings_theme_key)

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        LooprWalletApp.dagger.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_loopr_app)

        bindPreferenceSummaryToValue(findPreference(ThemeSettings.KEY_THEME), this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val stringValue = newValue?.toString() ?: return false

        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            val index = preference.findIndexOfValue(stringValue)

            val summary = if (index >= 0) preference.entries[index]
            else null

            // Set the summary to reflect the new value.
            preference.setSummary(summary)
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference?.summary = stringValue
        }

        val selectedTheme = themeSettings.getThemeFromSettings(stringValue)
        if (preference?.key == themeChangeKey && themeSettings.getCurrentTheme() != selectedTheme) {
            launch(UI) {
                // We are switching themes and need to recreate the back stack
                // We need to delay the launching so animations can finish
                delay(150L)

                activity?.let {
                    // TODO check this works from MainActivity
                    TaskStackBuilder.create(it)
                            .addNextIntent(Intent(it, MainActivity::class.java))
                            .addNextIntent(Intent(it, SettingsActivity::class.java))
                            .startActivities()
                }
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()

        preferenceScreen.onPreferenceChangeListener = null
    }

}