package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings.LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS
import com.caplaninnovations.looprwallet.utilities.RealmUtility

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletSettings(private val looprSettings: LooprSettings) {

    companion object {

        const val KEY_SECURITY_LOCKOUT_TIME_MILLIS = "_SECURITY_LOCKOUT_TIME_MILLIS"
        const val KEY_CURRENT_WALLET = "_CURRENT_WALLET"
        const val KEY_ALL_WALLETS = "_ALL_WALLETS"
        const val KEY_REALM_KEY = "_REALM_KEY"
    }

    /**
     * All of the possible values for lockout times (in millis)
     */
    object LockoutTimes {
        const val DEFAULT_LOCKOUT_TIME_MILLIS: Long = 1000 * 60
        const val FIVE_MINUTES_MILLIS: Long = 1000 * 60 * 5
        const val NONE_MILLIS: Long = 1000L

    }

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return looprSettings.getLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, DEFAULT_LOCKOUT_TIME_MILLIS)
    }

    /**
     * @param lockoutTime The time (in millis) from which the application will be locked after
     * leaving the foreground.
     */
    fun putLockoutTime(lockoutTime: Long) {
        looprSettings.putLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    /**
     * @return The current wallet being used by a user. Null means there is no current wallet and
     * the user must select or recover one
     */
    fun getCurrentWallet(): String? {
        return looprSettings.getString(KEY_CURRENT_WALLET)
    }

    /**
     * @return True if the wallet was selected, false otherwise
     */
    fun selectCurrentWallet(newCurrentWallet: String): Boolean {
        val doesContainWallet = getAllWallets().contains(newCurrentWallet)

        if (doesContainWallet) {
            looprSettings.putString(KEY_CURRENT_WALLET, newCurrentWallet)
        }

        return doesContainWallet
    }

    /**
     * Creates a wallet and sets the current wallet to this new one. Also creates a realm key and
     * adds the new wallet to the list of all wallets.
     *
     * @param newWalletName The new wallet's name which will be set to the current wallet

     * @return True if the wallet was created successfully or false otherwise. A return of false
     * is because a wallet with this name already exists
     */
    fun createWallet(newWalletName: String): Boolean {
        if (getAllWallets().contains(newWalletName)) {
            return false
        }

        putCurrentWallet(newWalletName)
        addWallet(newWalletName)
        putRealmKey(newWalletName, RealmUtility.createKey())

        return true
    }

    /**
     * Removes the given wallet from the device. This also removes the realm encryption key and
     * deletes the realm database.
     *
     * @param wallet The wallet to be removed from the device. This signs the current wallet out if
     * the selected wallet is the current one
     *
     * @return True if the wallet was removed or false otherwise
     */
    fun removeWallet(wallet: String): Boolean {
        var allWallets = getAllWallets()
        if (!allWallets.contains(wallet)) {
            return false
        }

        allWallets = allWallets.filterNot { it == wallet }.toTypedArray()

        putAllWallets(allWallets)
        putRealmKey(wallet, null)

        val newCurrentWallet = if (allWallets.isNotEmpty()) allWallets.first() else null
        putCurrentWallet(newCurrentWallet)

        return true
    }

    fun getRealmKey(walletName: String): ByteArray? {
        return looprSettings.getByteArray(KEY_REALM_KEY + walletName)
    }

    fun getAllWallets(): Array<String> {
        return looprSettings.getStringArray(KEY_ALL_WALLETS) ?: arrayOf()
    }

    // MARK - Private methods

    private fun addWallet(wallet: String) {
        val allWallets = looprSettings.getStringArray(KEY_ALL_WALLETS)?.toMutableList() ?: ArrayList()

        if (!allWallets.contains(wallet)) {
            allWallets.add(0, wallet)
            putAllWallets(allWallets.toTypedArray())
        }
    }

    private fun putRealmKey(walletName: String, key: ByteArray?) {
        looprSettings.putByteArray(KEY_REALM_KEY + walletName, key)
    }

    private fun putCurrentWallet(newWalletName: String?) {
        looprSettings.putString(KEY_CURRENT_WALLET, newWalletName)
    }

    private fun putAllWallets(allWallets: Array<String>) {
        looprSettings.putStringArray(KEY_ALL_WALLETS, allWallets)
    }

}