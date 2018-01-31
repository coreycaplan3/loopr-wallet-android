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
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash

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

    private lateinit var looprSettingsManager: LooprSettingsManager

    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        application = this

        looprSettingsManager = LooprSettingsManager(applicationContext)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun onActivityResumed(activity: Activity?) {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onActivityPaused(activity: Activity?) {
        val lockoutTime = looprSettingsManager.getLockoutTime()
        handler.postDelayed({
            val intent = Intent(applicationContext, SignInActivity::class.java)
            // TODO clear backstack and force sign in to be before everything else.
            // TODO Restore backstack on success

            applicationContext.startActivity(intent)
        }, lockoutTime)
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