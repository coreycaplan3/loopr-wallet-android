package org.loopring.looprwallet.core.wallet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.android.CipherClient
import org.loopring.looprwallet.appsecurity.activities.SecurityActivity
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.models.settings.UserWalletSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet

/**
 * Created by Corey on 3/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class WalletClientProductionImpl(context: Context, looprSecureSettings: LooprSecureSettings) : WalletClient {

    @Volatile
    private var isUnlocked = true

    private val handler by lazy {
        Handler()
    }

    private val walletSettings = UserWalletSettings(looprSecureSettings)

    private val cipherClient = CipherClient(context)

    override fun createWallet(walletName: String, privateKey: String): Boolean {
        return walletSettings.createWallet(walletName, privateKey)
    }

    override fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity) {
        walletSettings.selectCurrentWallet(newCurrentWallet)

        val intent = Intent(currentActivity, CoreLooprWalletApp.mainClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
    }

    override fun getCurrentWallet(): LooprWallet? = walletSettings.getCurrentWallet()

    override fun onNoCurrentWalletSelected(currentActivity: BaseActivity) {
        val intent = Intent(currentActivity, CoreLooprWalletApp.signInClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
        currentActivity.finish()
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
        if (lockoutTime != UserWalletSettings.LockoutTimes.NONE_MILLIS) {
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