package com.caplaninnovations.looprwallet.fragments.settings

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceChangeListener
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.allNonNull
import com.caplaninnovations.looprwallet.extensions.getResourceIdFromAttrId
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
abstract class BaseSettingsFragment : PreferenceFragmentCompat(), OnPreferenceClickListener,
        OnPreferenceChangeListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resourceId = view.context.theme.getResourceIdFromAttrId(R.attr.dividerColor)
        val color = ApplicationUtility.color(resourceId)
        this.setDivider(ColorDrawable(color))

        val height = ApplicationUtility.dimen(R.dimen.divider_height)
                .let { Math.round(it) }
        this.setDividerHeight(height)

        val listOfKeysAndDefaultValues = getPreferenceKeysAndDefaultValuesForListeners()
        listOfKeysAndDefaultValues.forEach {
            bindPreferenceSummaryToValue(it.first, it.second)
        }
    }

    /**
     * A function that retrieves the preference keys and default values for binding to the onChange
     * and onClick listeners.
     *
     * @return A list of pairs which contains the preference's key and its default value in the
     * first and second position, respectively.
     */
    abstract fun getPreferenceKeysAndDefaultValuesForListeners(): List<Pair<String, String>>

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val stringValue = newValue?.toString() ?: return false

        if (preference is ListPreference) {
            bindListPreferenceValue(preference, stringValue)
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference?.summary = stringValue
        }

        return true
    }

    // MARK - Protected Methods

    protected fun bindListPreferenceValue(preference: ListPreference, value: String) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        val index = preference.findIndexOfValue(value)

        val summary = if (index >= 0) preference.entries[index]
        else null

        // Set the summary to reflect the new value.
        preference.summary = summary
    }

    // MARK - Private Methods

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @param preferenceKey The key of the preference that will have the listeners bound to it.
     * @param defaultValue The default value to be displayed if there is currently no default
     * stored in preferences.
     */
    private fun bindPreferenceSummaryToValue(
            preferenceKey: String,
            defaultValue: String
    ) {
        val preference = findPreference(preferenceKey)

        val icon = preference.icon
        val primaryTextColorResource = context?.theme?.getResourceIdFromAttrId(android.R.attr.textColorSecondary)

        Pair(icon, primaryTextColorResource).allNonNull {
            val primaryTextColor = ApplicationUtility.color(it.second)
            DrawableCompat.setTint(it.first, primaryTextColor)
        }

        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = this
        preference.onPreferenceClickListener = this

        // Trigger the listener immediately with the preference's current value.
        val settings = LooprSettings.getInstance(LooprWalletApp.context)
        val value = settings.getString(preference.key) ?: defaultValue
        this.onPreferenceChange(preference, value)
    }

}