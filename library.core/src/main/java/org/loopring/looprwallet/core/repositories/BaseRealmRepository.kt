package org.loopring.looprwallet.core.repositories

import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.extensions.upsertCopyToRealm
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.deleteFromRealm
import org.loopring.looprwallet.core.application.LooprWalletCoreApp

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To persist information to a [Realm] and allow queries to be written against
 * [Realm] as well.
 *
 */
open class BaseRealmRepository(val currentWallet: LooprWallet)
    : BaseRepository<RealmModel> {

    private val realmClient = LooprWalletCoreApp.dagger.realmClient

    /**
     * A private realm instance that can only be accessed from the main thread.
     */
    val uiPrivateRealm = realmClient.getPrivateInstance(currentWallet.walletName, currentWallet.realmKey)

    /**
     * A shared realm instance that can only be accessed from the main thread.
     */
    val uiSharedRealm = realmClient.getSharedInstance()

    /**
     * ** THIS METHOD MUST BE CALLED FROM THE MAIN THREAD**
     *
     * Copies [data] to the [Realm] and returns it.
     *
     * @param data The data to copy into the [Realm]
     */
    fun <T : RealmModel> addAndReturn(data: T): T {
        executeSharedRealmTransaction { it.upsertCopyToRealm(data) }
        return data
    }

    fun runSharedTransaction(transaction: Realm.Transaction) {
        uiSharedRealm.executeTransaction(transaction)
    }

    fun runPrivateTransaction(transaction: Realm.Transaction) {
        uiPrivateRealm.executeTransaction(transaction)
    }

    final override fun add(data: RealmModel) {
        executeSharedRealmTransaction { it.upsert(data) }
    }

    final override fun addList(data: List<RealmModel>) {
        executeSharedRealmTransaction { it.upsert(data) }
    }

    final override fun remove(data: RealmModel) {
        executeSharedRealmTransaction { data.deleteFromRealm() }
    }

    final override fun remove(data: List<RealmModel>) {
        if (data is RealmResults) {
            executeSharedRealmTransaction { data.deleteAllFromRealm() }
        }
    }

    /**
     * ** THIS METHOD MUST BE CALLED FROM THE MAIN THREAD**
     *
     * Copies [data] to the [Realm] and returns it.
     *
     * @param data The data to copy into the [Realm]
     */
    fun <T : RealmModel> addAndReturnPrivateTransaction(data: T): T {
        executePrivateRealmTransaction { it.upsertCopyToRealm(data) }
        return data
    }

    fun addPrivateData(data: RealmModel) {
        executePrivateRealmTransaction { it.upsert(data) }
    }

    fun addListPrivateData(data: List<RealmModel>) {
        executePrivateRealmTransaction { it.upsert(data) }
    }

    fun removePrivateData(data: RealmModel) {
        executePrivateRealmTransaction { data.deleteFromRealm() }
    }

    fun removePrivateData(data: List<RealmModel>) {
        if (data is RealmResults) {
            executePrivateRealmTransaction { data.deleteAllFromRealm() }
        }
    }

    final override fun clear() {
        uiPrivateRealm.removeAllListenersAndClose()
        uiSharedRealm.removeAllListenersAndClose()
    }

    // MARK - Private Methods

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not [uiPrivateRealm]).
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executeSharedRealmTransaction(crossinline transaction: (Realm) -> T) {
        realmClient.getSharedInstance()
                .use {
                    it.executeTransaction { transaction(it) }
                }
    }

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not a UI realm).
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executePrivateRealmTransaction(crossinline transaction: (Realm) -> T) {
        realmClient.getPrivateInstance(currentWallet.walletName, currentWallet.realmKey)
                .use {
                    it.executeTransaction { transaction(it) }
                }
    }

}