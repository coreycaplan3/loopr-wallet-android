package com.caplaninnovations.looprwallet.models.android.settings

import java.util.*

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
        private const val MAX_NUMBER_OF_SEQUENTIAL_GUESSES = 4
        private const val LOCKOUT_TIME_MILLIS: Long = 1000 * 60 * 5

        private const val KEY_USER_PIN = "_USER_PIN"

        /**
         * A key that maps to a number between 0 and 4. At 4, the user becomes locked out
         */
        private const val KEY_NUMBER_OF_SEQUENTIAL_TRIES = "_NUMBER_OF_SEQUENTIAL_TRIES"

        /**
         * A key that maps to the last time the user had a lockout from too many bad guesses
         */
        private const val KEY_LAST_LOCKOUT_TIME = "_LAST_LOCKOUT_TIME"
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

    /**
     * Checks if the two PINs are equal and increments the number of sequential failures if the
     * user is incorrect.
     *
     * @return True if the PINs match or false if they don't.
     */
    fun checkPinAndIncrementAttemptsIfFailure(pin: String): Boolean {
        if (isUserPinEqual(pin)) return true

        var numberOfTries = looprSecureSettings.getInt(KEY_NUMBER_OF_SEQUENTIAL_TRIES, 0)
        numberOfTries += 1

        if (numberOfTries >= MAX_NUMBER_OF_SEQUENTIAL_GUESSES) {
            numberOfTries = 0
            looprSecureSettings.putLong(KEY_LAST_LOCKOUT_TIME, Date().time)
        }

        return false
    }

    /**
     * @return True if the user is currently locked out or false otherwise
     */
    fun isUserLockedOut(): Boolean {
        val lockoutTime = looprSecureSettings.getLong(KEY_LAST_LOCKOUT_TIME, -1)
        return lockoutTime == -1L || lockoutTime + LOCKOUT_TIME_MILLIS >= Date().time
    }

    /**
     * @return The amount of lockout time left (in millis) or -1 if the user is NOT locked out. It
     * is expected you call [isUserLockedOut] before calling this method.
     */
    fun getLockoutTimeLeft(): Long = when {
        isUserLockedOut() -> {
            val lockoutTime = looprSecureSettings.getLong(KEY_LAST_LOCKOUT_TIME, -1)
            lockoutTime + LOCKOUT_TIME_MILLIS - Date().time
        }
        else -> -1
    }

}