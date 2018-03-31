package com.caplaninnovations.looprwallet.realm

import android.support.annotation.VisibleForTesting
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.utilities.BuildUtility.BUILD_DEBUG
import com.caplaninnovations.looprwallet.utilities.BuildUtility.BUILD_RELEASE
import com.caplaninnovations.looprwallet.utilities.BuildUtility.BUILD_STAGING
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

        private const val SHARED_REALM_NAME = "shared-loopr"

        /**
         * An instance of [RealmClient] that can be used for the any user (not private info).
         */
        fun getSharedInstance(): RealmClient {
            val buildType = BuildConfig.BUILD_TYPE
            return when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }

        /**
         * An instance of [RealmClient] that can be used for the user's private Realm instance.
         */
        fun getPrivateInstance(): RealmClient {
            val buildType = BuildConfig.BUILD_TYPE
            return when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }
    }

    abstract val schemaVersion: Long

    abstract fun getSharedInstance(): Realm

    abstract fun getPrivateInstance(realmName: String, encryptionKey: ByteArray): Realm

    @VisibleForTesting
    fun getPrivateRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .migration(LooprPrivateInstanceMigration())
                .initialData(InitialRealmPrivateData.getInitialData())
                .schemaVersion(schemaVersion)
    }

    @VisibleForTesting
    fun getSharedRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .migration(LooprSharedInstanceMigration())
                .initialData(InitialRealmSharedData.getInitialData())
                .schemaVersion(schemaVersion)
    }

    private class RealmClientDebugImpl : RealmClient() {

        override val schemaVersion: Long
            get() = 0

        override fun getPrivateInstance(realmName: String, encryptionKey: ByteArray): Realm {
            val configuration = getPrivateRealmConfigurationBuilder("$realmName-in-memory")
                    .deleteRealmIfMigrationNeeded()
                    .inMemory()
                    .build()

            return Realm.getInstance(configuration)
        }

        override fun getSharedInstance(): Realm {
            val configuration = getSharedRealmConfigurationBuilder(SHARED_REALM_NAME)
                    .deleteRealmIfMigrationNeeded()
                    .inMemory()
                    .build()

            return Realm.getInstance(configuration)
        }
    }

    private class RealmClientProductionImpl : RealmClient() {

        override val schemaVersion: Long
            get() = 0

        override fun getPrivateInstance(realmName: String, encryptionKey: ByteArray): Realm {
            val configuration = getPrivateRealmConfigurationBuilder(realmName)
                    .encryptionKey(encryptionKey)
                    .build()

            return Realm.getInstance(configuration)
        }

        override fun getSharedInstance(): Realm {
            val configuration = getSharedRealmConfigurationBuilder(SHARED_REALM_NAME)
                    .build()

            return Realm.getInstance(configuration)
        }

    }

}