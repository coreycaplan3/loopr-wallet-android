package org.loopring.looprwallet.core.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.activities.TestActivity
import org.loopring.looprwallet.core.dagger.*
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.utilities.PreferenceUtility
import org.loopring.looprwallet.core.wallet.WalletClient
import javax.inject.Inject

/**
 * Created by Corey Caplan on 4/2/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class CoreLooprWalletApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
        CoreLooprComponentProvider {

    companion object {

        /**
         * The class representing the *MainActivity* for the app
         */
        lateinit var mainClass: Class<out BaseActivity>

        lateinit var signInClass: Class<out BaseActivity>

        lateinit var application: Application

        val context: Context
            get() = application

        val coreLooprComponent by lazy {
            buildCoreComponent(CoreLooprWalletApp.context, DaggerCoreLooprComponent.builder())
        }

        private fun buildCoreComponent(context: Context, builder: DaggerCoreLooprComponent.Builder): CoreLooprComponent =
                builder.looprSettingsModule(LooprSettingsModule(context))
                        .looprSecureSettingsModule(LooprSecureSettingsModule(context))
                        .looprRealmModule(LooprRealmModule())
                        .looprWalletModule(LooprWalletModule(context))
                        .build()

    }

    @Inject
    lateinit var walletClient: WalletClient

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        CoreLooprWalletApp.application = this
        CoreLooprWalletApp.mainClass = TestActivity::class.java

        PreferenceUtility.setDefaultValues()

        Realm.init(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun provideCoreLooprComponent() = coreLooprComponent

    override fun onActivityResumed(activity: Activity?) {
        activity?.let {
            if (!walletClient.isUnlocked()) {
                walletClient.unlockLooprApp(it)
            }
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.let {
            walletClient.beginLockCountdown()
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

}