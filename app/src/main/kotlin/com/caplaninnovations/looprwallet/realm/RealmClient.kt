package com.caplaninnovations.looprwallet.realm

import com.caplaninnovations.looprwallet.BuildConfig
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Corey Caplan on 1/30/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class RealmClient {

    companion object {

        fun getInstance(): RealmClient {
            val buildType = BuildConfig.BUILD_TYPE
            return when (buildType) {
                "debug" -> RealmClientDebugImpl()
                "staging", "release" -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }
    }

    abstract val schemaVersion: Long

    abstract fun getInstance(realmName: String, encryptionKey: ByteArray): Realm

    protected fun getRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .migration(LooprMigration())
                .initialData(InitialRealmData.getInitialData())
                .schemaVersion(schemaVersion)
    }

    private class RealmClientDebugImpl : RealmClient() {

        override val schemaVersion: Long
            get() = 0

        override fun getInstance(realmName: String, encryptionKey: ByteArray): Realm {
            val configuration = getRealmConfigurationBuilder("$realmName-in-memory")
                    .deleteRealmIfMigrationNeeded()
                    .inMemory()
                    .build()

            return Realm.getInstance(configuration)
        }

    }

    private class RealmClientProductionImpl : RealmClient() {

        override val schemaVersion: Long
            get() = 0

        override fun getInstance(realmName: String, encryptionKey: ByteArray): Realm {
            val configuration = getRealmConfigurationBuilder(realmName)
                    .encryptionKey(encryptionKey)
                    .build()

            return Realm.getInstance(configuration)
        }

    }

}