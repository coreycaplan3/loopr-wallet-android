package com.caplaninnovations.looprwallet.realm

import io.realm.Realm

/**
 *  Created by Corey on 2/5/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
class RealmClientImplTest : RealmClient() {

    override val schemaVersion: Long
        get() = RealmClient.getInstance().schemaVersion

    override fun getInstance(realmName: String, encryptionKey: ByteArray): Realm {
        val configuration = getRealmConfigurationBuilder(realmName, encryptionKey)
                .inMemory()
                .build()
        return Realm.getInstance(configuration)
    }
}