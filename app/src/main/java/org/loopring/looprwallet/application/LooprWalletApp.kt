package org.loopring.looprwallet.application

import com.google.firebase.FirebaseApp
import com.google.firebase.crash.FirebaseCrash
import org.loopring.looprwallet.contacts.dagger.ContactsLooprComponent
import org.loopring.looprwallet.contacts.dagger.ContactsLooprComponentProvider
import org.loopring.looprwallet.contacts.dagger.DaggerContactsLooprComponent
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.homeorders.dagger.DaggerHomeOrdersLooprComponent
import org.loopring.looprwallet.homeorders.dagger.HomeOrdersLooprComponent
import org.loopring.looprwallet.homeorders.dagger.HomeOrdersLooprComponentProvider
import org.loopring.looprwallet.createtransfer.dagger.DaggerTransferLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.TransferLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.TransferLooprComponentProvider
import org.loopring.looprwallet.walletsignin.dagger.DaggerWalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponentProvider

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
open class LooprWalletApp : CoreLooprWalletApp(), ContactsLooprComponentProvider,
        HomeOrdersLooprComponentProvider, TransferLooprComponentProvider,
        WalletLooprComponentProvider {

    companion object {

        val contactsLooprComponent: ContactsLooprComponent by lazy {
            DaggerContactsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeOrdersLooprComponent: HomeOrdersLooprComponent by lazy {
            DaggerHomeOrdersLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val transferLooprComponent: TransferLooprComponent by lazy {
            DaggerTransferLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val walletLooprComponent: WalletLooprComponent by lazy {
            DaggerWalletLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

    }

    override fun provideContactsLooprComponent() = contactsLooprComponent

    override fun provideHomeOrdersLooprComponent() = homeOrdersLooprComponent

    override fun provideTransferLooprComponent() = transferLooprComponent

    override fun provideWalletLooprComponent() = walletLooprComponent

    override fun onCreate() {
        super.onCreate()

        CoreLooprWalletApp.mainClass = MainActivity::class.java

        FirebaseApp.initializeApp(this)
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

}