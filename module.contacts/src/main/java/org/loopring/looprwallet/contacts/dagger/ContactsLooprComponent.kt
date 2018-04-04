package org.loopring.looprwallet.contacts.dagger

import dagger.Component
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope

/**
 * Created by Corey on 4/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */

@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface ContactsLooprComponent {

    fun inject(createContactDialog: CreateContactDialog)
}

interface ContactsLooprComponentProvider {

    fun provideContactsLooprComponent(): ContactsLooprComponent
}

val contactsLooprComponent: ContactsLooprComponent
    get() = (LooprWalletCoreApp.application as ContactsLooprComponentProvider).provideContactsLooprComponent()