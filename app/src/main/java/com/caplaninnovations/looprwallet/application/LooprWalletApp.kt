package com.caplaninnovations.looprwallet.application

import org.loopring.looprwallet.contacts.dagger.ContactsLooprComponentProvider
import org.loopring.looprwallet.contacts.dagger.DaggerContactsLooprComponent
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.transfer.dagger.DaggerTransferLooprComponent
import org.loopring.looprwallet.transfer.dagger.TransferLooprComponentProvider
import org.loopring.looprwallet.walletsignin.dagger.DaggerWalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponentProvider

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class LooprWalletApp : CoreLooprWalletApp(), ContactsLooprComponentProvider,
        TransferLooprComponentProvider, WalletLooprComponentProvider {

    companion object {

        val contactsLooprComponent by lazy {
            DaggerContactsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val transferLooprComponent by lazy {
            DaggerTransferLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val walletLooprComponent by lazy {
            DaggerWalletLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

    }

    override fun provideContactsLooprComponent() = contactsLooprComponent

    override fun provideTransferLooprComponent() = transferLooprComponent

    override fun provideWalletLooprComponent() = walletLooprComponent

    override fun onCreate() {
        super.onCreate()

        CoreLooprWalletApp.mainClass = MainActivity::class.java
    }

}