package org.loopring.looprwallet.core.realm

import io.realm.Realm
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.contact.Contact

/**
 *  Created by Corey on 4/26/2018.
 *
 *  Project: loopr-android
 *
 *  Purpose of Class:
 *
 */
object InitialRealmPrivateData {

    fun getInitialData() = Realm.Transaction {
        InitialRealmData.getTransactionBody(it)

        it.upsert(Contact("0x1111111111111111111111111111111111111111", "Daniel"))
        it.upsert(Contact("0x2222222222222222222222222222222222222222", "Jay"))
        it.upsert(Contact("0x3333333333333333333333333333333333333333", "Corey"))
        it.upsert(Contact("0x4444444444444444444444444444444444444444", "Adam"))
    }

}