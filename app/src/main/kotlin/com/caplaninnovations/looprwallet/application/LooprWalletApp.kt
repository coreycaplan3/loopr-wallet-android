package com.caplaninnovations.looprwallet.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.dagger.*
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.utilities.PreferenceUtility
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

        val context: Context
            get() = LooprWalletCoreApp.application.applicationContext

        val dagger: LooprDaggerComponent
            get() = LooprWalletCoreApp.application.looprDaggerComponent

    }

    val looprDaggerComponent: LooprDaggerComponent by lazy {
        provideDaggerComponent()
    }

    @Inject
    lateinit var walletClient: WalletClient

    @Inject
    lateinit var ethereumClient: Web3j

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        LooprWalletCoreApp.application = this

        PreferenceUtility.setDefaultValues()

        Realm.init(this)

        looprDaggerComponent.inject(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    open fun provideDaggerComponent(): LooprDaggerComponent {
        return DaggerLooprDaggerComponent.builder()
                .looprSettingsModule(LooprSettingsModule(applicationContext))
                .looprSecureSettingsModule(LooprSecureSettingsModule(applicationContext))
                .looprRealmModule(LooprRealmModule())
                .looprWalletModule(LooprWalletModule(applicationContext))
                .looprEthereumBlockchainModule(LooprEthereumBlockchainModule())
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