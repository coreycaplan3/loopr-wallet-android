package org.loopring.looprwallet.core.realm

import android.support.annotation.VisibleForTesting
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.mapIfNull
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_DEBUG
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_RELEASE
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_STAGING
import org.loopring.looprwallet.core.utilities.RealmUtility

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

        private const val REALM_NAME = "loopr-android"

        private const val KEY_REALM_ENCRYPTION_KEY = "loopr-android-encryption-key"

        /**
         * An instance of [RealmClient] that can be used for the any user (not private info).
         */
        fun getInstance(): RealmClient {
            val buildType = BuildUtility.BUILD_TYPE
            return when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }

        /**
         * Initializes the migration and initial data from a background thread. This should be
         * called from the application instance's *onCreate*.
         */
        fun initializeMigrationAndInitialDataAsync() = async(IO) {
            val buildType = BuildUtility.BUILD_TYPE
            val client = when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }

            val configuration = client.getSharedRealmConfigurationBuilder(REALM_NAME)
                    .migration(LooprRealmMigration())
                    .initialData(InitialRealmData.getInitialData())
                    .build()

            Realm.getInstance(configuration)
                    .close()
        }
    }

    abstract val schemaVersion: Long

    private val encryptionKey: ByteArray

    init {
        val settings = LooprSecureSettings.getInstance(CoreLooprWalletApp.context)
        encryptionKey = getEncryptionKeyFromSettings(settings)
    }

    abstract fun getInstance(): Realm

    @VisibleForTesting
    fun getEncryptionKeyFromSettings(settings: LooprSecureSettings) = settings.getByteArray(KEY_REALM_ENCRYPTION_KEY)
            .mapIfNull {
                RealmUtility.createKey().also { settings.putByteArray(KEY_REALM_ENCRYPTION_KEY, it) }
            }

    @VisibleForTesting
    fun getSharedRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .encryptionKey(encryptionKey)
                .modules(CoreLooprRealmModule())
                .schemaVersion(schemaVersion)
    }

    private class RealmClientDebugImpl : RealmClient() {

        override val schemaVersion = 0L

        override fun getInstance(): Realm {
            val configuration = getSharedRealmConfigurationBuilder(REALM_NAME)
                    .deleteRealmIfMigrationNeeded()
                    .inMemory()
                    .build()

            return Realm.getInstance(configuration)
        }
    }

    private class RealmClientProductionImpl : RealmClient() {

        override val schemaVersion = 0L

        override fun getInstance(): Realm {
            val configuration = getSharedRealmConfigurationBuilder(REALM_NAME)
                    .build()

            return Realm.getInstance(configuration)
        }

    }

}