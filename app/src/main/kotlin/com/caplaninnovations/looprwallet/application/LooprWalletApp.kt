package com.caplaninnovations.looprwallet.application

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.multidex.MultiDexApplication
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.activities.SignInActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsManager
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import io.realm.android.internal.android.crypto.SyncCryptoFactory

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        lateinit var application: LooprWalletApp
    }

    private lateinit var looprSettingsManager: LooprWalletSettings

    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        application = this

        looprSettingsManager = LooprWalletSettings(applicationContext)

        Realm.init(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    @Volatile
    var shouldUnlockApp: Boolean = false

    override fun onActivityResumed(activity: Activity?) {
        if (shouldUnlockApp && activity != null) {
            SyncCryptoFactory.get(activity).unlock_keystore()
        }
        handler.removeCallbacksAndMessages(null)
        shouldUnlockApp = false
    }

    override fun onActivityPaused(activity: Activity?) {
        val lockoutTime = looprSettingsManager.getLockoutTime()
        handler.postDelayed({ shouldUnlockApp = true }, lockoutTime)
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

}