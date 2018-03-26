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

        // Fix the divider resource
        val resourceId = view.context.theme.getResourceIdFromAttrId(R.attr.dividerColor)
        val color = ApplicationUtility.color(resourceId)
        this.setDivider(ColorDrawable(color))

        val height = ApplicationUtility.dimen(R.dimen.divider_height)
                .let { Math.round(it) }
        this.setDividerHeight(height)

        // Bind the default and current values
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

    /**
     * Called whenever a preference changes and when the fragment is initially setup via list
     * return from [getPreferenceKeysAndDefaultValuesForListeners].
     */
    final override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val stringValue = newValue?.toString() ?: return false

        // TODO Make call to preference summary change in appropriate place.
        // TODO Keep in mind, we're only calling it now when we "successfully" change preference
        // TODO values, as seen below

        // TODO Make an abstract method that sets the summary's value based on the key, preference
        // TODO value. This can use other preference's state to mutate the summary's value as
        // TODO appropriate
        preference?.let {
            if (onPreferenceChange(preference, stringValue)) {
                bindPreferenceValueToSummary(preference, stringValue)
                savePreferenceToSettings(preference, stringValue)
            }
        }

        return false
    }

    // MARK - Protected Methods

    /**
     * @return True if the preference should be persisted to [LooprSettings] or false otherwise.
     * It is expected that a return of false will be handled in the subclass for its own specific
     * reasons.
     */
    protected abstract fun onPreferenceChange(preference: Preference, value: String): Boolean

    // MARK - Protected Methods

    protected fun bindPreferenceValueToSummary(preference: Preference?, value: String) {
        if (preference is ListPreference) {
            bindListPreferenceValue(preference, value)
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference?.summary = value
        }
    }

    /**
     * Use this method to persist values to preferences, instead of the PreferenceManager. Reason
     * being, it allows us to decouple persisted settings (settings that are saved to disk) from
     * testing, to make settings not interrupt testing. It also allows for more overall control.
     */
    protected fun savePreferenceToSettings(preference: Preference, value: String) =
            LooprSettings.getInstance(LooprWalletApp.context)
                    .putString(preference.key, value)

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

    /**
     * Looks up the correct display value in the preference's 'entries' list and binds it to the
     * summary.
     */
    private fun bindListPreferenceValue(preference: ListPreference, value: String) {
        val index = preference.findIndexOfValue(value)

        val summary = if (index >= 0) preference.entries[index]
        else null

        // Set the summary to reflect the new value.
        preference.summary = summary
    }

}