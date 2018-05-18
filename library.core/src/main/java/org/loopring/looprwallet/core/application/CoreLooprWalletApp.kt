package org.loopring.looprwallet.core.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import io.realm.Realm
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.activities.CoreTestActivity
import org.loopring.looprwallet.core.dagger.*
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.realm.RealmClient
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
open class CoreLooprWalletApp : MultiDexApplication(), ActivityLifecycleCallbacks,
        CoreLooprComponentProvider {

    companion object {

        /**
         * A realm that can be accessed from only the [UI] co-routine context/thread
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var uiGlobalRealm: Realm

        /**
         * A realm that can be accessed from only the [IO] co-routine context/thread
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var ioGlobalRealm: Realm

        /**
         * A realm that can be accessed from only the [NET] co-routine context/thread
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var netGlobalRealm: Realm

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
    lateinit var realmClient: RealmClient

    @Inject
    lateinit var walletClient: WalletClient

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        CoreLooprWalletApp.application = this
        CoreLooprWalletApp.mainClass = CoreTestActivity::class.java

        coreLooprComponent.inject(this)

        async(IO) {
            PreferenceUtility.setDefaultValues()
        }

        Realm.init(this)

        runBlocking {
            RealmClient.initializeMigrationAndInitialDataAsync()
                    .await()
        }

        async(UI) { uiGlobalRealm = realmClient.getInstance() }

        async(IO) { ioGlobalRealm = realmClient.getInstance() }

        async(NET) { netGlobalRealm = realmClient.getInstance() }

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