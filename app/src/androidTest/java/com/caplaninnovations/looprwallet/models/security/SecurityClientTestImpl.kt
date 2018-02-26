package com.caplaninnovations.looprwallet.models.security

import android.app.Activity
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.utilities.loge

/**
 * Created by Corey on 2/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class SecurityClientTestImpl(looprSettings: LooprSettings) : SecurityClient {

    private val walletSettings = WalletSettings(looprSettings)

    override fun createWallet(walletName: String, privateKey: String): Boolean {
        return walletSettings.createWallet(walletName, privateKey)
    }

    override fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity) {
        loge("Stub code: selectNewCurrentWallet")
    }

    override fun getCurrentWallet(): LooprWallet? {
        return walletSettings.getCurrentWallet()
    }

    override fun onNoCurrentWalletSelected(currentActivity: BaseActivity) {
        loge("Stub code: onNoCurrentWalletSelected")
    }

    override fun isUnlocked(): Boolean {
        loge("Stub code: isUnlocked")
        return true
    }

    override fun unlockLooprApp(activity: Activity) {
        loge("Stub code: unlockLooprApp")
    }

    override fun isAndroidKeystoreUnlocked(): Boolean {
        loge("Stub code: isAndroidKeystoreUnlocked")
        return true
    }

    override fun unlockAndroidKeystore() {
        loge("Stub code: unlockAndroidKeystore")
    }

    override fun beginLockCountdown() {
        loge("Stub code: beginLockCountdown")
    }

    override fun stopLockCountdown() {
        loge("Stub code: stopLockCountdown")
    }

    override fun removeWallet(walletName: String): Boolean {
        return walletSettings.removeWallet(walletName)
    }

}