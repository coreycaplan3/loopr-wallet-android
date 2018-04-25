package org.loopring.looprwallet.core.realm

import android.support.annotation.VisibleForTesting
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_DEBUG
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_RELEASE
import org.loopring.looprwallet.core.utilities.BuildUtility.BUILD_STAGING
import org.loopring.looprwallet.core.wallet.WalletClient

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
        fun initializeMigrationAndInitialDataAsync(walletClient: WalletClient) = async(CommonPool) {
            val buildType = BuildUtility.BUILD_TYPE
            val client = when (buildType) {
                BUILD_DEBUG -> RealmClientDebugImpl()
                BUILD_STAGING, BUILD_RELEASE -> RealmClientProductionImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }

            val configuration = client.getSharedRealmConfigurationBuilder(SHARED_REALM_NAME)
                    .migration(LooprRealmMigration())
                    .initialData(InitialRealmSharedData.getInitialData())
                    .build()

            val realm = Realm.getInstance(configuration)
            realm.close()

            launch(UI) {
                val realmClient = RealmClient.getInstance()
                CoreLooprWalletApp.uiSharedRealm = realmClient.getSharedInstance()

                walletClient.getCurrentWallet()?.let {
                    CoreLooprWalletApp.uiPrivateRealm = realmClient.getPrivateInstance(it)
                }
            }

            Unit
        }
    }

    abstract val schemaVersion: Long

    abstract fun getSharedInstance(): Realm

    /**
     * @param wallet The wallet instance that is tied to this private realm
     */
    abstract fun getPrivateInstance(wallet: LooprWallet): Realm

    @VisibleForTesting
    fun getPrivateRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .modules(CoreLooprRealmModule())
                .schemaVersion(schemaVersion)
    }

    @VisibleForTesting
    fun getSharedRealmConfigurationBuilder(realmName: String): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(realmName)
                .modules(CoreLooprRealmModule())
                .schemaVersion(schemaVersion)
    }

    private class RealmClientDebugImpl : RealmClient() {

        override val schemaVersion = 0L

        override fun getPrivateInstance(wallet: LooprWallet): Realm {
            val configuration = getPrivateRealmConfigurationBuilder("${wallet.walletName}-in-memory")
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

        override val schemaVersion = 0L

        override fun getPrivateInstance(wallet: LooprWallet): Realm {
            val configuration = getPrivateRealmConfigurationBuilder(wallet.walletName)
                    .encryptionKey(wallet.realmKey)
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