package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings.LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletSettings(context: Context) : LooprSettingsManager(context) {

    companion object {

        private const val KEY_CURRENT_WALLET = "_CURRENT_WALLET"
        private const val KEY_CURRENT_REALM = "CURRENT_REALM"
    }

    /**
     * All of the possible values for lockout times (in millis)
     */
    object LockoutTimes {
        const val DEFAULT_LOCKOUT_TIME_MILLIS: Long = 1000 * 60
    }

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return getLong(Keys.KEY_SECURITY_LOCKOUT_TIME_MILLIS, DEFAULT_LOCKOUT_TIME_MILLIS)
    }

    /**
     * @param lockoutTime The time (in millis) from which the application will be locked after
     * leaving the foreground.
     */
    fun putLockoutTime(lockoutTime: Long) {
        putLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    /**
     * @return The current wallet being used by a user. Null means there is no current wallet and
     * the user must select or recover one
     */
    fun getCurrentWallet(): String? {
        return getString(KEY_CURRENT_WALLET)
    }

    /**
     * @param newWalletName The new wallet to be currently used. Can be null to "logout". Doing so
     * should bring the user back to the sign in screen.
     */
    fun changeCurrentWallet(newWalletName: String?) {
        putString(KEY_CURRENT_WALLET, newWalletName)
    }

    fun getRealmKey(walletName: String): ByteArray? {
        return getByteArray(walletName)
    }

    fun putRealmKey(walletName: String, key: ByteArray) {
        putByteArray(walletName, key)
    }

}