package com.caplaninnovations.looprwallet.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import android.support.v4.app.ActivityCompat
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.dagger.*
import com.caplaninnovations.looprwallet.handlers.PermissionHandler
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import org.web3j.protocol.Web3j
import javax.inject.Inject

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
open class LooprWalletApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        lateinit var application: LooprWalletApp

        fun getContext(): Context = application.applicationContext
    }

    val looprProductionComponent: LooprProductionComponent by lazy {
        provideDaggerComponent()
    }

    @Inject
    lateinit var walletClient: WalletClient

    @Inject
    lateinit var ethereumClient: Web3j

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        application = this

        ActivityCompat.setPermissionCompatDelegate(PermissionHandler.delegate)

        Realm.init(this)

        looprProductionComponent.inject(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    open fun provideDaggerComponent(): LooprProductionComponent {
        return DaggerLooprProductionComponent.builder()
                .looprSettingsModule(LooprSettingsModule(applicationContext))
                .looprRealmModule(LooprRealmModule())
                .looprSecurityModule(LooprSecurityModule(applicationContext))
                .looprEthModule(LooprEthModule())
                .build()
    }

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