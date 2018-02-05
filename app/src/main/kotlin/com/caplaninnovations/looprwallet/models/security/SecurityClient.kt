package com.caplaninnovations.looprwallet.models.security

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.caplaninnovations.looprwallet.activities.*
import com.caplaninnovations.looprwallet.models.android.settings.*
import com.caplaninnovations.looprwallet.utilities.loge
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.android.CipherClient

/**
 *  Created by Corey on 2/4/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
interface SecurityClient {

    companion object {

        fun getInstance(context: Context, looprSettings: LooprSettings): SecurityClient {
            return SecurityClientImplProduction(context, looprSettings)
        }

    }

    /**
     * Attempts to create a wallet with the given name. If a wallet with that name already exists,
     * the creation will fail. Upon creating the wallet, the new wallet will also become the
     * selected one.
     *
     * @param walletName The name of the new wallet
     * @return True if the wallet was created successfully, or false otherwise. A return of false
     * means that a wallet with this name already exists
     */
    fun createWallet(walletName: String): Boolean

    /**
     * Selects a new current-wallet (from an existing list) and resets the current activity task
     * list, starting at [MainActivity]
     *
     * @param newCurrentWallet The new wallet that is now selected
     * @param currentActivity The current activity. This parameter is used to restart the current
     * activity, since other configurations need to be reinitialized to support the new wallet.
     */
    fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity)

    /**
     * @return The current wallet being used by the user. A null return values means there is no
     * current wallet selected.
     */
    fun getCurrentWallet(): String?

    /**
     * @return The encryption key to be used with the current wallet (as returned by
     * [getCurrentWallet]. A null return means there is no current wallet selected.
     */
    fun getCurrentWalletEncryptionKey(): ByteArray?

    /**
     * Called when there is **NO** current wallet selected by the user. A call to this method
     * should result in the user being sent to the [SignInActivity]
     *
     * @param currentActivity The current activity that is on-screen for the user. This parameter
     * is used to make a call to [BaseActivity.finish] and start the [SignInActivity] from the same
     * task stack.
     */
    fun onNoCurrentWalletSelected(currentActivity: BaseActivity)

    /**
     * @return True if the app is now unlocked or false otherwise. If the app is locked, a call to
     * [unlockLooprApp] is expected, since it will prompt the user to unlock the application using
     * a stored password.
     */
    fun isUnlocked(): Boolean

    /**
     * Unlocks our app for normal use. If the app is unlocked, calling this function is a no-op.
     * **NOTE**: Failing to unlock the app by the user will result in the app being closed.
     *
     * @param activity The current activity, as seen by the user. It is used to start the
     * [SecurityActivity].
     */
    fun unlockLooprApp(activity: Activity)

    /**
     * @return True if the *Android Keystore* is unlocked or false otherwise. This is different from
     * [isUnlocked], since is dependent on the Android OS being locked or unlocked. This is
     * **REQUIRED** since our app relies on the Keystore being unlocked to safely access and store
     * encryption keys.
     */
    fun isAndroidKeystoreUnlocked(): Boolean

    /**
     * Unlocks the *Android Keystore*. If it is already unlocked, calling this function is a no-op.
     * Note, this is
     */
    fun unlockAndroidKeystore()

    /**
     * Begins a locking countdown. After the specified time, [isUnlocked] should return false and
     * the app should be inaccessible, without entering a password. The countdown is based on the
     * time set using [WalletSettings]
     */
    fun beginLockCountdown()

    /**
     * Stops the locking countdown, if it began. If there is no countdown occurring, this is a
     * no-op
     */
    fun stopLockCountdown()

    /**
     * This method removes the inputted wallet from settings and deletes its respective database.
     * **NOTE: ALL REALMS OPEN WITH THE INPUTTED WALLET'S NAME SHOULD BE CLOSED OR ELSE AN
     * EXCEPTION WILL BE THROWN**
     *
     * @param walletName Removes the inputted wallet from the app. If this wallet is the selected
     * wallet, the next wallet is selected. Otherwise, the selected wallet remains unchanged
     *
     * @return True if the wallet was successfully removed or false otherwise. A return value of
     * false indicates the wallet doesn't exist.
     */
    fun removeWallet(walletName: String): Boolean
}

private class SecurityClientImplProduction(context: Context, looprSettings: LooprSettings) : SecurityClient {

    @Volatile
    private var isUnlocked = true

    private val handler = Handler()

    private val walletSettings = WalletSettings(looprSettings)

    private val cipherClient = CipherClient(context)

    override fun createWallet(walletName: String): Boolean {
        return walletSettings.createWallet(walletName)
    }

    override fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity) {
        walletSettings.selectCurrentWallet(newCurrentWallet)

        val intent = Intent(currentActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
    }

    override fun getCurrentWallet(): String? = walletSettings.getCurrentWallet()

    override fun getCurrentWalletEncryptionKey(): ByteArray? {
        val currentWallet = getCurrentWallet()

        return if (currentWallet != null) {
            walletSettings.getRealmKey(currentWallet)
        } else {
            null
        }
    }

    override fun onNoCurrentWalletSelected(currentActivity: BaseActivity) {
        val intent = Intent(currentActivity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
    }

    override fun isUnlocked(): Boolean = isUnlocked

    override fun unlockLooprApp(activity: Activity) {
        activity.startActivity(Intent(activity, SecurityActivity::class.java))
    }

    override fun isAndroidKeystoreUnlocked(): Boolean = cipherClient.isKeystoreUnlocked

    override fun unlockAndroidKeystore() {
        if (!cipherClient.isKeystoreUnlocked) {
            cipherClient.unlockKeystore()
        }
    }

    override fun beginLockCountdown() {
        val lockoutTime = walletSettings.getLockoutTime()
        if (lockoutTime != WalletSettings.LockoutTimes.NONE_MILLIS) {
            handler.postDelayed({ isUnlocked = false }, lockoutTime)
        }
    }

    override fun stopLockCountdown() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun removeWallet(walletName: String): Boolean {
        if (!walletSettings.removeWallet(walletName)) {
            // GUARD
            return false
        }

        val configuration = RealmConfiguration.Builder()
                .name(walletName)
                .build()

        return try {
            Realm.deleteRealm(configuration)
        } catch (e: Exception) {
            loge("An error occurred while deleting the Realm!", e)
            false
        }
    }
}