package org.loopring.looprwallet.core.wallet

import android.app.Activity
import android.content.Context
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet

/**
 *  Created by Corey on 2/4/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
interface WalletClient {

    companion object {

        private var instance: WalletClient? = null

        fun getInstance(context: Context, looprSecureSettings: LooprSecureSettings): WalletClient = synchronized(this) {
            val instance = this.instance
            if (instance != null) {
                return@synchronized instance
            }

            logd("Initializing currentWallet client...")
            return@synchronized WalletClientProductionImpl(context, looprSecureSettings).also {
                this.instance = it
            }
        }

    }

    fun setOnCurrentWalletChange(onChange: ((LooprWallet) -> Unit)?)

    /**
     * Attempts to create a wallet with the given name. If a wallet with that name or private key
     * already exists, the creation will fail. Upon creating the wallet, the new wallet will also
     * become the selected one.
     *
     * @param walletName The name of the new wallet
     * @param privateKey The private key of the new wallet
     * @param keystoreContent The keystore content used to create the wallet or null if it wasn't
     * used to create the wallet.
     * @param phrase The phrase used to create the wallet or null if it wasn't used to create the
     * wallet.
     * @return True if the wallet was created successfully, or false otherwise. A return of false
     * means that a wallet with this name or private key already exists
     */
    fun createWallet(walletName: String,
                     privateKey: String,
                     keystoreContent: String?,
                     phrase: Array<String>?): Boolean

    /**
     * Selects a new current-wallet (from an existing list). Typically, an activity restart should
     * occur afterward.
     *
     * @param newCurrentWallet The new wallet that is now selected
     */
    fun selectNewCurrentWallet(newCurrentWallet: String)

    /**
     * @return The current wallet being used by the user. A null return values means there is no
     * current wallet selected.
     */
    fun getCurrentWallet(): LooprWallet?

    /**
     * @return The list of [LooprWallet]s that are on the device currently in alphabetical order.
     * Note, the list will be empty if there are no wallets on the device currently.
     */
    fun getAllWallets(): List<LooprWallet>

    /**
     * Called when there is **NO** current wallet selected by the user. A call to this method
     * should result in the user being sent to the [SignInActivity]
     *
     * @param currentActivity The current activity that is on-screen for the user. This parameter
     * is used to make a call to [BaseActivity.finish] and start the [SignInActivity] from the same
     * task stack.
     */
    fun noCurrentWalletSelected(currentActivity: BaseActivity)

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
     * time set using [UserWalletSettings]
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
     * wallet, the next wallet is selected. Otherwise, the selected wallet remains unchanged. This
     * wallet variable should already be cleansed (all lower case, correct formatting, etc.)
     *
     * @return True if the wallet was successfully removed or false otherwise. A return value of
     * false indicates the wallet doesn't exist.
     */
    fun removeWallet(walletName: String): Boolean
}