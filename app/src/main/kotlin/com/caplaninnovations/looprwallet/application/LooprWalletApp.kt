package com.caplaninnovations.looprwallet.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.support.multidex.MultiDexApplication
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.dagger.DaggerLooprProductionComponent
import com.caplaninnovations.looprwallet.dagger.LooprProductionComponent
import com.caplaninnovations.looprwallet.dagger.LooprSettingsModule
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
open class LooprWalletApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        lateinit var application: LooprWalletApp
    }

    private val handler = Handler()

    lateinit var looprProductionComponent: LooprProductionComponent

    @Inject
    lateinit var looprSettings: LooprSettings

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        application = this

        Realm.init(this)

        looprProductionComponent = provideDaggerComponent()
        looprProductionComponent.inject(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    open fun provideDaggerComponent(): LooprProductionComponent {
        return DaggerLooprProductionComponent.builder()
                .looprSettingsModule(LooprSettingsModule(applicationContext))
                .build()
    }

    @Volatile
    var shouldUnlockApp: Boolean = false

    override fun onActivityResumed(activity: Activity?) {
        if (shouldUnlockApp && activity != null) {
            // TODO
//            SyncCryptoFactory.get(activity).unlock_keystore()
        }
        handler.removeCallbacksAndMessages(null)
        shouldUnlockApp = false
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.let {
            val lockoutTime = LooprWalletSettings(looprSettings).getLockoutTime()
            if (lockoutTime != LooprWalletSettings.LockoutTimes.NONE_MILLIS) {
                handler.postDelayed({ shouldUnlockApp = true }, lockoutTime)
            }
        }
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