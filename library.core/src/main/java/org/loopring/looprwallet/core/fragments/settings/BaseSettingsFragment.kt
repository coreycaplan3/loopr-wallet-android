package org.loopring.looprwallet.core.fragments.settings

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.*
import android.support.v7.preference.Preference.OnPreferenceChangeListener
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.view.View
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.allNonNull
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
abstract class BaseSettingsFragment : PreferenceFragmentCompat(), OnPreferenceClickListener,
        OnPreferenceChangeListener {

    @CallSuper
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val settings = LooprSettings.getInstance(context!!)
        preferenceManager.preferenceDataStore = settings.preferenceDataStore
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fix the divider resource
        val resourceId = view.context.theme.getResourceIdFromAttrId(R.attr.dividerColor)
        val color = ApplicationUtility.col(resourceId)
        this.setDivider(ColorDrawable(color))

        val height = ApplicationUtility.dimen(R.dimen.divider_height)
                .let { Math.round(it) }
        this.setDividerHeight(height)

        // Bind the default and current values
        val listOfKeysAndDefaultValues = getPreferenceKeysAndDefaultValues()
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
    abstract fun getPreferenceKeysAndDefaultValues(): List<Pair<String, String>>

    /**
     * The title of the fragment, which will be bound to the action bar
     */
    abstract val fragmentTitle: String

    /**
     * Based on a key and preference value, returns the summary value (which is displayed in the UI
     * to the user).
     *
     * This method is only called internally from within [bindPreferenceValueToSummary]
     *
     * @return A human-readable summary value for the user
     * @see getSummaryForListPreference
     */
    abstract fun getSummaryValue(preference: Preference, value: String): String

    /**
     * Called whenever a preference changes and when the fragment is initially setup via list
     * return from [getPreferenceKeysAndDefaultValues].
     */
    final override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val stringValue = newValue?.toString() ?: return false

        return when {
            preference != null && onPreferenceValueChange(preference, stringValue) -> {
                bindPreferenceValueToSummary(preference, stringValue)
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as? AppCompatActivity)?.supportActionBar?.title = fragmentTitle
    }

    // MARK - Protected Methods

    /**
     * @return True if the preference should be persisted to [LooprSettings] or false otherwise.
     * It is expected that a return of false will be handled in the subclass for its own specific
     * reasons.
     *
     * Note: If you return true, a call to [getSummaryValue] will occur right after.
     */
    protected abstract fun onPreferenceValueChange(preference: Preference, value: String): Boolean

    // MARK - Protected Methods

    /**
     * Binds the given preference's value to its summary. This method gets the summary's value by
     * calling [getSummaryValue] on the [value] provided.
     *
     * @param preference The preference whose summary value will be set
     * @param value The preference value that will be used in the call to [getSummaryValue] and
     * bound to the preference's summary
     */
    protected fun bindPreferenceValueToSummary(preference: Preference?, value: String) {
        if (preference != null) {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.summary = getSummaryValue(preference, value)
        }
    }

    /**
     * Looks up the correct display value in the preference's *entries* list and binds it to the
     * summary.
     *
     * @param preference The list preference whose summary will be bound to a human-readable value
     * @param value The entry-value, which will be used in the lookup in the [ListPreference]'s
     * entries.
     */
    protected fun getSummaryForListPreference(preference: ListPreference, value: String): String {
        val index = preference.findIndexOfValue(value)

        // Set the summary to reflect the new value.
        return if (index >= 0) preference.entries[index].toString()
        else {
            loge("Could not get preference for $preference")
            value
        }
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
        val textColorResource = context?.theme?.getResourceIdFromAttrId(android.R.attr.textColorSecondary)

        Pair(icon, textColorResource).allNonNull {
            val primaryTextColor = ApplicationUtility.col(it.second)
            DrawableCompat.setTint(it.first, primaryTextColor)
        }

        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = this
        preference.onPreferenceClickListener = this

        // Trigger the listener immediately with the preference's current value.
        val settings = LooprSettings.getInstance(CoreLooprWalletApp.context)

        if (preference is SeekBarPreference) {
            val value = settings.getInt(preference.key, defaultValue.toInt())
            this.onPreferenceChange(preference, value)
        } else {
            val value = settings.getString(preference.key) ?: defaultValue
            this.onPreferenceChange(preference, value)
        }
    }

}