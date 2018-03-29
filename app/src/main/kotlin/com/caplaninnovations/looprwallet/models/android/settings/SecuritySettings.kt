package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.loge
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SecuritySettings(private val looprSettings: LooprSettings) {

    companion object {

        /**
         * Key used for accessing the different types of security settings.
         *
         * @see TYPE_DEFAULT_VALUE_SECURITY
         * @see TYPE_PIN_SECURITY
         */
        val PREFERENCE_KEY_SECURITY_TYPE = ApplicationUtility.str(R.string.settings_security_type_key)

        /**
         * Key used for accessing the different security timeouts.
         */
        val PREFERENCE_KEY_SECURITY_TIMEOUT = ApplicationUtility.str(R.string.settings_security_timeout_key)

        // Timeout Types

        val DEFAULT_SECURITY_TIMEOUT = ApplicationUtility.str(R.string.settings_security_timeout_entries_values_default)

        // Security Types

        /**
         * Defaults to "none" or no security
         */
        val TYPE_DEFAULT_VALUE_SECURITY = ApplicationUtility.str(R.string.settings_security_type_entries_values_default)

        /**
         * Value used for 4-digit PIN security
         */
        val TYPE_PIN_SECURITY = ApplicationUtility.str(R.string.settings_security_type_entries_values_pin)

    }

    fun getCurrentSecurityType(): String {
        return looprSettings.getString(PREFERENCE_KEY_SECURITY_TYPE) ?: TYPE_DEFAULT_VALUE_SECURITY
    }

    fun getCurrentSecurityTimeout(): Int {
        val defaultValue = DEFAULT_SECURITY_TIMEOUT.toInt()
        return looprSettings.getInt(PREFERENCE_KEY_SECURITY_TIMEOUT, defaultValue)
    }

    fun setCurrentSecurityType(securityType: String) {
        looprSettings.putString(PREFERENCE_KEY_SECURITY_TYPE, checkSecurityType(securityType))
    }

    // MARK - Private Methods

    /**
     * Checks that the provided value is indeed a security type and logs an exception otherwise.
     */
    private fun checkSecurityType(value: String) = when (value) {
        TYPE_PIN_SECURITY -> value
        else -> {
            loge("Invalid security type, found: $value")
            TYPE_DEFAULT_VALUE_SECURITY
        }
    }

}