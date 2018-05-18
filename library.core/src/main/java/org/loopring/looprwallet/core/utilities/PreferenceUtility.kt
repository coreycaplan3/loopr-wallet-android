package org.loopring.looprwallet.core.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.support.v14.preference.MultiSelectListPreference
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.*
import android.support.v7.preference.PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.R.xml
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.models.settings.LooprSettings
import kotlin.reflect.KProperty

/**
 * Created by Corey Caplan on 3/27/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object PreferenceUtility {

    fun setDefaultValues() {
        setDefaultValues(R.xml.settings_home, xml::settings_home)
        setDefaultValues(R.xml.settings_security, xml::settings_security)
        setDefaultValues(R.xml.settings_ethereum_fees, xml::settings_ethereum_fees)
        setDefaultValues(R.xml.settings_loopring_fees, xml::settings_loopring_fees)
        setDefaultValues(R.xml.settings_general_wallet, xml::settings_general_wallet)
        setDefaultValues(R.xml.settings_currency, xml::settings_currency)
        setDefaultValues(R.xml.settings_loopring_network, xml::settings_loopring_network)
    }

    /**
     * Similar to [.setDefaultValues] but allows
     * the client to provide the filename and mode of the shared preferences
     * file.
     *
     * @param resId The resource ID of the preference XML file.
     *
     * Note: this will NOT reset preferences back to their default
     * values. For that functionality, use
     * [PreferenceManager.getDefaultSharedPreferences]
     * and clear it followed by a call to this method with this
     * parameter set to true.
     *
     * @see .setDefaultValues
     * @see .setSharedPreferencesName
     * @see .setSharedPreferencesMode
     */
    @SuppressLint("RestrictedApi")
    private fun setDefaultValues(resId: Int, resourceName: KProperty<Int>) {
        val context = CoreLooprWalletApp.context
        val settings = LooprSettings.getInstance(context)

        val key = KEY_HAS_SET_DEFAULT_VALUES + resourceName
        if (!settings.getBoolean(key, false)) {
            val pm = PreferenceManager(context)
            pm.preferenceDataStore = settings.preferenceDataStore
            pm.sharedPreferencesName = getDefaultSharedPreferencesName(context)
            pm.sharedPreferencesMode = defaultSharedPreferencesMode

            val preferenceScreen = pm.inflateFromResource(context, resId, null)
            persistPreferenceGroup(preferenceScreen, resourceName.name)

            settings.putBoolean(key, true)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun persistPreferenceGroup(preferenceGroup: PreferenceGroup, resourceName: String) {
        loop@ for (i in 0..(preferenceGroup.preferenceCount - 1)) {
            val preference = preferenceGroup.getPreference(i)
            when {
                preference is PreferenceGroup -> persistPreferenceGroup(preference, resourceName)

                Preference::class.simpleName != preference::class.simpleName -> {
                    // Plain old Preference tags aren't used with default values.
                    if (!preference.hasKey()) {
                        throw IllegalStateException("No key found, was it forgotten? Resource: $resourceName Class: ${preference::class.simpleName}")
                    }

                    val field = Preference::class.java.getDeclaredField("mDefaultValue")
                    field.isAccessible = true

                    if (field.get(preference) == null) {
                        throw IllegalStateException("Invalid default value for preference with key: ${preference.key}")
                    }

                    val value = field.get(preference).toString()
                    when (preference) {
                        is ListPreference -> preference.value = value
                        is EditTextPreference -> preference.text = value
                        is SeekBarPreference -> preference.value = value.toInt()
                        is SwitchPreference -> preference.isChecked = value.toBoolean()
                        is CheckBoxPreference -> preference.isChecked = value.toBoolean()
                        is DropDownPreference -> preference.value = value
                        is MultiSelectListPreference -> preference.values = field.get(preference) as Set<String>
                    }
                }
            }
        }
    }

    fun getDefaultSharedPreferencesName(context: Context): String {
        return context.packageName + "_preferences"
    }

    const val defaultSharedPreferencesMode = Context.MODE_PRIVATE

}