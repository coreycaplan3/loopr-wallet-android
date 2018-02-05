package com.caplaninnovations.looprwallet.realm

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Corey Caplan on 1/30/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
abstract class RealmClient {

    companion object {

        fun getInstance(): RealmClient {
            return RealmClientImplProduction()
        }

    }

    abstract val schemaVersion: Long

    abstract fun getInstance(realmName: String, encryptionKey: ByteArray): Realm

    protected fun getRealmConfigurationBuilder(realmName: String, encryptionKey: ByteArray): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .encryptionKey(encryptionKey)
                .migration(LooprMigration())
                .schemaVersion(schemaVersion)
    }

}

private class RealmClientImplProduction : RealmClient() {

    override val schemaVersion: Long
        get() = 0

    override fun getInstance(realmName: String, encryptionKey: ByteArray): Realm {
        val configuration = getRealmConfigurationBuilder(realmName, encryptionKey)
                .build()

        return Realm.getInstance(configuration)
    }

}