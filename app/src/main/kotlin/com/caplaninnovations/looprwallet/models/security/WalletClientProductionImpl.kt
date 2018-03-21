package com.caplaninnovations.looprwallet.models.security

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.SecurityActivity
import com.caplaninnovations.looprwallet.activities.SignInActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.extensions.loge
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.android.CipherClient

/**
 * Created by Corey on 3/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class WalletClientProductionImpl(context: Context, looprSettings: LooprSettings) : WalletClient {

    @Volatile
    private var isUnlocked = true

    private val handler by lazy {
        Handler()
    }

    private val walletSettings = WalletSettings(looprSettings)

    private val cipherClient = CipherClient(context)

    override fun createWallet(walletName: String, privateKey: String): Boolean {
        return walletSettings.createWallet(walletName, privateKey)
    }

    override fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity) {
        walletSettings.selectCurrentWallet(newCurrentWallet)

        val intent = Intent(currentActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
    }

    override fun getCurrentWallet(): LooprWallet? = walletSettings.getCurrentWallet()

    override fun onNoCurrentWalletSelected(currentActivity: BaseActivity) {
        val intent = Intent(currentActivity, SignInActivity::class.java)
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
            loge("An addErrorObserver occurred while deleting the Realm!", e)
            false
        }
    }
}