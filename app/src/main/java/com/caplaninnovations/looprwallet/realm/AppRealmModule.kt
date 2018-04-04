package com.caplaninnovations.looprwallet.realm

import org.loopring.looprwallet.contacts.realm.ContactsLooprRealmModule
import org.loopring.looprwallet.core.realm.CoreLooprRealmModule

/**
 * Created by Corey on 4/4/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object AppRealmModule {

    val ALL_MODULES: Array<*>
        get() = arrayOf(
                CoreLooprRealmModule(),
                ContactsLooprRealmModule()
        )

}