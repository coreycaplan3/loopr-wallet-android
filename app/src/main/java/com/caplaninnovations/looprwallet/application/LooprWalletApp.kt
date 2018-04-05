package com.caplaninnovations.looprwallet.application

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.dagger.AppLooprComponent
import com.caplaninnovations.looprwallet.dagger.DaggerAppLooprComponent
import com.google.firebase.crash.FirebaseCrash
import io.realm.Realm
import org.loopring.looprwallet.contacts.dagger.ContactsLooprComponent
import org.loopring.looprwallet.contacts.dagger.ContactsLooprComponentProvider
import org.loopring.looprwallet.contacts.dagger.DaggerContactsLooprComponent
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.dagger.*
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.realm.RealmClient
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.PreferenceUtility
import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.transfer.dagger.DaggerTransferLooprComponent
import org.loopring.looprwallet.transfer.dagger.TransferLooprComponent
import org.loopring.looprwallet.transfer.dagger.TransferLooprComponentProvider
import org.loopring.looprwallet.walletsignin.dagger.DaggerWalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponentProvider
import javax.inject.Inject

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
open class LooprWalletApp : MultiDexApplication(), ActivityLifecycleCallbacks,
        CoreLooprComponentProvider, ContactsLooprComponentProvider, TransferLooprComponentProvider,
        WalletLooprComponentProvider {

    @Inject
    lateinit var walletClient: WalletClient

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        LooprWalletCoreApp.application = this
        LooprWalletCoreApp.mainClass = MainActivity::class.java

        BuildUtility.BUILD_TYPE = BuildConfig.BUILD_TYPE
        BuildUtility.BUILD_FLAVOR = BuildConfig.ENVIRONMENT
        BuildUtility.SECURITY_LOCKOUT_TIME = BuildConfig.SECURITY_LOCKOUT_TIME

        PreferenceUtility.setDefaultValues()

        Realm.init(this)

        appLooprComponent.inject(this)

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun provideCoreLooprComponent() = coreLooprComponent

    override fun provideContactsLooprComponent() = contactsLooprComponent

    override fun provideTransferLooprComponent() = transferLooprComponent

    override fun provideWalletLooprComponent() = walletLooprComponent

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

    companion object {

        private var internalCoreLooprComponent: CoreLooprComponent? = null
        private var internalAppLooprComponent: AppLooprComponent? = null
        private var internalContactsLooprComponent: ContactsLooprComponent? = null
        private var internalTransferLooprComponent: TransferLooprComponent? = null
        private var internalWalletLooprComponent: WalletLooprComponent? = null

        val coreLooprComponent: CoreLooprComponent
            get() = synchronized(this) {
                if (internalCoreLooprComponent == null) {
                    val context = LooprWalletCoreApp.context
                    internalCoreLooprComponent = buildCoreComponent(context, DaggerCoreLooprComponent.builder())
                }
                return@synchronized internalCoreLooprComponent!!
            }

        val appLooprComponent: AppLooprComponent
            get() = synchronized(this) {
                if (internalAppLooprComponent == null) {
                    internalAppLooprComponent = this.buildSubComponent(DaggerAppLooprComponent.builder())
                }
                return@synchronized internalAppLooprComponent!!
            }

        val contactsLooprComponent: ContactsLooprComponent
            get() = synchronized(this) {
                if (internalContactsLooprComponent == null) {
                    internalContactsLooprComponent = buildSubComponent(DaggerContactsLooprComponent.builder())
                }
                return@synchronized internalContactsLooprComponent!!
            }

        val transferLooprComponent: TransferLooprComponent
            get() = synchronized(this) {
                if (internalTransferLooprComponent == null) {
                    internalTransferLooprComponent = buildSubComponent(DaggerTransferLooprComponent.builder())
                }
                return@synchronized internalTransferLooprComponent!!
            }

        val walletLooprComponent: WalletLooprComponent
            get() = synchronized(this) {
                if (internalWalletLooprComponent == null) {
                    internalWalletLooprComponent = buildSubComponent(DaggerWalletLooprComponent.builder())
                }
                return@synchronized internalWalletLooprComponent!!
            }

        private fun buildCoreComponent(context: Context, builder: DaggerCoreLooprComponent.Builder): CoreLooprComponent =
                builder.looprSettingsModule(LooprSettingsModule(context))
                        .looprSecureSettingsModule(LooprSecureSettingsModule(context))
                        .looprRealmModule(LooprRealmModule())
                        .looprWalletModule(LooprWalletModule(context))
                        .build()

        private fun buildSubComponent(builder: DaggerAppLooprComponent.Builder): AppLooprComponent =
                builder.coreLooprComponent(coreLooprComponent).build()

        private fun buildSubComponent(builder: DaggerContactsLooprComponent.Builder): ContactsLooprComponent =
                builder.coreLooprComponent(coreLooprComponent).build()

        private fun buildSubComponent(builder: DaggerTransferLooprComponent.Builder): TransferLooprComponent =
                builder.coreLooprComponent(coreLooprComponent).build()

        private fun buildSubComponent(builder: DaggerWalletLooprComponent.Builder): WalletLooprComponent =
                builder.coreLooprComponent(coreLooprComponent).build()
    }

}