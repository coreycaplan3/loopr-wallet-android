package org.loopring.looprwallet.core.realm

import android.support.annotation.VisibleForTesting
import io.realm.Realm
import io.realm.RealmConfiguration
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_DEBUG
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_RELEASE
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_STAGING

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

        var allRealmModules: Array<*>? = null

        /**
         * An instance of [RealmClient] that can be used for the any user (not private info).
         */
        fun getSharedInstance(): RealmClient {
            val buildType = BuildUtility.BUILD_TYPE
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
            val buildType = BuildUtility.BUILD_TYPE
            return when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }
    }

    abstract val privateSchemaVersion: Long
    abstract val sharedSchemaVersion: Long

    abstract fun getSharedInstance(): Realm

    abstract fun getPrivateInstance(realmName: String, encryptionKey: ByteArray): Realm

    @VisibleForTesting
    fun getPrivateRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .modules(allRealmModules
                        ?: throw IllegalArgumentException("Realm modules cannot be null"))
                .migration(LooprPrivateInstanceMigration())
                .initialData(InitialRealmPrivateData.getInitialData())
                .schemaVersion(privateSchemaVersion)
    }

    @VisibleForTesting
    fun getSharedRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .modules(allRealmModules
                        ?: throw IllegalArgumentException("Realm modules cannot be null"))
                .migration(LooprSharedInstanceMigration())
                .initialData(InitialRealmSharedData.getInitialData())
                .modules(CoreLooprRealmModule())
                .schemaVersion(sharedSchemaVersion)
    }

    private class RealmClientDebugImpl : RealmClient() {

        override val privateSchemaVersion = 0L
        override val sharedSchemaVersion = 0L

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

        override val privateSchemaVersion = 0L
        override val sharedSchemaVersion = 0L

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