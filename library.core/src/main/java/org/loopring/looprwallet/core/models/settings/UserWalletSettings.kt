package org.loopring.looprwallet.core.models.settings

import android.support.annotation.VisibleForTesting
import org.loopring.looprwallet.core.models.settings.UserWalletSettings.LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.utilities.RealmUtility
import org.loopring.looprwallet.core.wallet.WalletClient

/**
 * Created by Corey Caplan on 1/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: **This class should not be touched directly. Instead, use [WalletClient].** To
 * interact directly with settings, putting or getting values. This class does not contain much
 * logic, just core essentials. Logic associated with this class can be seen in conjunction with
 * [WalletClient].
 */
class UserWalletSettings(private val looprSecureSettings: LooprSecureSettings) {

    companion object {

        const val KEY_SECURITY_LOCKOUT_TIME_MILLIS = "_SECURITY_LOCKOUT_TIME_MILLIS"
        const val KEY_CURRENT_WALLET = "_CURRENT_WALLET"
        const val KEY_ALL_WALLETS = "_ALL_WALLETS"
        const val KEY_REALM_KEY = "_REALM_KEY"
        const val KEY_PRIVATE_KEY = "_PRIVATE_KEY"
        const val KEY_PASSPHRASE = "_PASSPHRASE"
        const val KEY_KEYSTORE_CONTENT = "_KEYSTORE_CONTENT"
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
        return looprSecureSettings.getLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, DEFAULT_LOCKOUT_TIME_MILLIS)
    }

    /**
     * @param lockoutTime The time (in millis) from which the application will be locked after
     * leaving the foreground.
     */
    fun putLockoutTime(lockoutTime: Long) {
        looprSecureSettings.putLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    /**
     * @return The current wallet being used by a user. A null return value means there is no
     * current wallet and the user must create, select or recover one
     */
    fun getCurrentWallet(): LooprWallet? {
        val walletName = looprSecureSettings.getString(KEY_CURRENT_WALLET)
        return walletName?.let {
            val realmKey = getRealmKey(it)
            val privateKey = getPrivateKey(it)
            val keystoreContent = getKeystoreContent(walletName)!!
            val passphrase = getPassphrase(walletName)!!

            return when {
                realmKey == null || privateKey == null -> null
                else -> LooprWallet(walletName, realmKey, keystoreContent, passphrase, privateKey)
            }
        }
    }

    /**
     * @return The current wallet being used by a user. A null return value means there is no
     * current wallet and the user must create, select or recover one. The wallet variable
     * should already be cleansed (all lower case, correct formatting, etc.)
     */
    fun getWallet(walletName: String): LooprWallet? {
        return if (!getAllWalletNames().contains(walletName)) {
            return null
        } else {
            val realmKey = getRealmKey(walletName)!!
            val privateKey = getPrivateKey(walletName)!!
            val keystoreContent = getKeystoreContent(walletName)!!
            val passphrase = getPassphrase(walletName)!!
            LooprWallet(walletName, realmKey, keystoreContent, passphrase, privateKey)
        }
    }

    /**
     * @return True if the wallet was selected, false otherwise
     */
    fun selectCurrentWallet(newCurrentWallet: String): Boolean {
        val doesContainWallet = getAllWalletNames().contains(newCurrentWallet)

        if (doesContainWallet) {
            looprSecureSettings.putString(KEY_CURRENT_WALLET, newCurrentWallet)
        }

        return doesContainWallet
    }

    /**
     * Creates a wallet and sets the current wallet to this new one. Also creates a realm key and
     * adds the new wallet to the list of all wallets.
     *
     * @param newWalletName The new wallet's name which will be set to the current wallet. The
     * wallet variable should already be cleansed (all lower case, correct formatting, etc.)
     * @param privateKey The verified/cleansed private key
     * @param keyStoreContent The content of the Keystore file, if the wallet was created using
     * this mechanism (or null otherwise)
     * @param passphrase The 12-word phrase, if the wallet was created using this mechanism (or null
     * otherwise).

     * @return True if the wallet was created successfully or false otherwise. A return of false
     * is because a wallet with this name already exists
     */
    fun createWallet(
            newWalletName: String,
            privateKey: String,
            keyStoreContent: String?,
            passphrase: Array<String>?
    ): Boolean {
        if (getAllWalletNames().contains(newWalletName)) {
            return false
        }

        if (getAllWalletNames().any({ getPrivateKey(it) == privateKey })) {
            // There's already a wallet with this private key
            return false
        }

        putCurrentWallet(newWalletName)
        addWallet(newWalletName)
        putPrivateKey(newWalletName, privateKey)
        putRealmKey(newWalletName, RealmUtility.createKey())
        putKeystoreContent(newWalletName, keyStoreContent)
        putPassphrase(newWalletName, passphrase)

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
        val oldAllWallets = getAllWalletNames()
        if (!oldAllWallets.contains(wallet)) {
            return false
        }

        val allWallets = oldAllWallets.filterNot { it == wallet }.toTypedArray()

        putAllWallets(allWallets)
        putRealmKey(wallet, null)
        putKeystoreContent(wallet, null)
        putPassphrase(wallet, null)
        putPrivateKey(wallet, null)

        if (getCurrentWallet()?.walletName == wallet) {
            // We just deleted the currentWallet, so we must reset the new one
            val newCurrentWallet = when {
                allWallets.isNotEmpty() -> allWallets.first()
                else -> null
            }
            putCurrentWallet(newCurrentWallet)
        }

        return true
    }

    /**
     * @return All of the [LooprWallet]s that are currently stored in settings in alphabetical
     * order.
     */
    fun getAllWallets(): List<LooprWallet> {
        return getAllWalletNames().mapNotNull { getWallet(it) }
    }

    // MARK - Private methods

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun getAllWalletNames(): Array<String> {
        return (looprSecureSettings.getStringArray(KEY_ALL_WALLETS) ?: arrayOf())
                .apply {
                    sort()
                }
    }

    /**
     * Adds the given wallet to the list of all wallets
     */
    private fun addWallet(wallet: String) {
        val allWallets = looprSecureSettings.getStringArray(KEY_ALL_WALLETS)?.toMutableList()
                ?: ArrayList()

        if (!allWallets.contains(wallet)) {
            allWallets.add(0, wallet)
            putAllWallets(allWallets.toTypedArray())
        }
    }

    private fun getPrivateKey(walletName: String): String? {
        return looprSecureSettings.getString(KEY_PRIVATE_KEY + walletName)
    }

    private fun putPrivateKey(walletName: String, privateKey: String?) {
        looprSecureSettings.putString(KEY_PRIVATE_KEY + walletName, privateKey)
    }

    // REALM KEY

    private fun putRealmKey(walletName: String, key: ByteArray?) {
        looprSecureSettings.putByteArray(KEY_REALM_KEY + walletName, key)
    }

    @VisibleForTesting
    fun getRealmKey(walletName: String): ByteArray? {
        return looprSecureSettings.getByteArray(KEY_REALM_KEY + walletName)
    }

    // PASSPHRASE

    private fun putPassphrase(walletName: String, passphrase: Array<String>?) {
        looprSecureSettings.putStringArray(KEY_PASSPHRASE + walletName, passphrase)
    }

    private fun getPassphrase(walletName: String): Array<String>? {
        return looprSecureSettings.getStringArray(KEY_PASSPHRASE + walletName)
    }

    // KEYSTORE

    private fun putKeystoreContent(walletName: String, keyStoreContent: String?) {
        looprSecureSettings.putString(KEY_KEYSTORE_CONTENT + walletName, keyStoreContent)
    }

    private fun getKeystoreContent(walletName: String): String? {
        return looprSecureSettings.getString(KEY_KEYSTORE_CONTENT + walletName)
    }

    // CURRENT WALLET

    private fun putCurrentWallet(newWalletName: String?) {
        looprSecureSettings.putString(KEY_CURRENT_WALLET, newWalletName)
    }

    private fun putAllWallets(allWallets: Array<String>) {
        looprSecureSettings.putStringArray(KEY_ALL_WALLETS, allWallets)
    }

}