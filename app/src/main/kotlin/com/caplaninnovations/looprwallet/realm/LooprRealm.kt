package com.caplaninnovations.looprwallet.realm

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Corey Caplan on 1/30/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object LooprRealm {

    private const val schemaVersion: Long = 0L

    fun get(realmName: String, encryptionKey: ByteArray): Realm {
        val configuration = RealmConfiguration.Builder()
                .name(realmName)
                .encryptionKey(encryptionKey)
                .migration(LooprMigration())
                .schemaVersion(schemaVersion)
                .build()

        return Realm.getInstance(configuration)
    }

}