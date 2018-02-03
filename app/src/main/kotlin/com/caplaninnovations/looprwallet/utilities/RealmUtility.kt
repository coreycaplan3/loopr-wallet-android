package com.caplaninnovations.looprwallet.utilities

import android.content.Intent
import android.util.Base64
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.SignInActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import com.caplaninnovations.looprwallet.realm.LooprRealm
import io.realm.Realm
import java.security.SecureRandom

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object RealmUtility {

    fun createKey(): ByteArray {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    fun initialize(activity: BaseActivity): Realm? {
        val walletSettings = LooprWalletSettings(activity)

        val currentWallet = walletSettings.getCurrentWallet()
        activity.currentWallet = currentWallet

        if (currentWallet == null) {
            val intent = Intent(activity, SignInActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
            return null
        } else {
            val walletKey = walletSettings.getRealmKey(currentWallet)

            return if (walletKey != null) {
                logv("Successfully retrieved wallet key: ${Base64.encodeToString(walletKey, Base64.DEFAULT)}")
                LooprRealm.get(currentWallet, walletKey)
            } else {
                loge("Why is the walletKey null?", IllegalStateException())
                null
            }
        }
    }

    fun closeAllRealmInstances(realmInstanceName: String, activity: BaseActivity) {
        activity.realm?.close()

        activity.supportFragmentManager.fragments.forEach {
            (it as? BaseFragment)?.realm?.close()
        }
    }

}