package com.caplaninnovations.looprwallet.models.security

import android.app.Activity
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.utilities.loge

/**
 *  Created by Corey on 2/4/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
class SecurityClientImplTest(looprSettings: LooprSettings) : SecurityClient {

    private val walletSettings = WalletSettings(looprSettings)

    override fun createWallet(walletName: String): Boolean {
        return walletSettings.createWallet(walletName)
    }

    override fun selectNewCurrentWallet(newCurrentWallet: String, currentActivity: BaseActivity) {
        loge("Stub code: selectNewCurrentWallet")
    }

    override fun getCurrentWallet(): String? {
        return walletSettings.getCurrentWallet()
    }

    override fun getCurrentWalletEncryptionKey(): ByteArray? {
        val currentWallet = getCurrentWallet()
        return when (currentWallet) {
            is String -> walletSettings.getRealmKey(currentWallet)
            else -> null
        }
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