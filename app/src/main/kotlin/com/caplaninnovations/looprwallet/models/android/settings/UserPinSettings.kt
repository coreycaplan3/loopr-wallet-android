package com.caplaninnovations.looprwallet.models.android.settings

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class whose sole responsibility is to store the app's PIN via an encrypted
 * storage.
 */
class UserPinSettings(private val looprSecureSettings: LooprSecureSettings) {

    companion object {
        private const val KEY_USER_PIN = "_USER_PIN"
    }

    /**
     * Sets the user's PIN in a secure storage and encrypts it
     */
    fun setUserPin(pin: String) = looprSecureSettings.putString(KEY_USER_PIN, pin)

    /**
     * Checks if the entered PIN matches the user's stored PIN
     *
     * @return True if they're equal or false otherwise
     */
    fun isUserPinEqual(pin: String) = looprSecureSettings.getString(KEY_USER_PIN) == pin

}