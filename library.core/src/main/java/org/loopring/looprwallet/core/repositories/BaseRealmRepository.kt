package org.loopring.looprwallet.core.repositories

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.deleteFromRealm
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.extensions.removeAllListenersAndClose
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.extensions.upsertCopyToRealm
import org.loopring.looprwallet.core.realm.RealmClient
import javax.inject.Inject

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To persist information to a [Realm] and allow queries to be written against
 * [Realm] as well.
 *
 */
abstract class BaseRealmRepository(isPrivateInstance: Boolean) : BaseRepository<RealmModel> {

    @Inject
    lateinit var realmClient: RealmClient

    /**
     * @return An instance of a *private* or *shared* realm for use when executing transactions or
     * performing queries.
     */
    protected abstract fun getAsyncRealm(): Realm

    /**
     * This realm can **ONLY** be used from the UI thread
     */
    protected val uiRealm: Realm

    init {
        coreLooprComponent.inject(this)

        uiRealm = when(isPrivateInstance) {
            true -> CoreLooprWalletApp.uiPrivateRealm
            else -> CoreLooprWalletApp.uiSharedRealm
        }
    }

    /**
     * ** THIS METHOD MUST BE CALLED FROM THE MAIN THREAD**
     *
     * Copies [data] to the [Realm] and returns it.
     *
     * @param data The data to copy into the [Realm]
     */
    fun <T : RealmModel> addAndReturn(data: T): T {
        return executeTransactionAndReturn { it.upsertCopyToRealm(data) }
    }

    fun runTransaction(transaction: Realm.Transaction) {
        getAsyncRealm().use { it.executeTransaction(transaction) }
    }

    final override fun add(data: RealmModel) {
        getAsyncRealm().use { it.executeTransaction { it.upsert(data) } }
    }

    final override fun addList(data: List<RealmModel>) {
        getAsyncRealm().use { it.executeTransaction { it.upsert(data) } }
    }

    final override fun remove(data: RealmModel) {
        getAsyncRealm().use { it.executeTransaction { data.deleteFromRealm() } }
    }

    final override fun remove(data: List<RealmModel>) {
        if (data is RealmResults) {
            getAsyncRealm().use { it.executeTransaction { data.deleteAllFromRealm() } }
        }
    }

    // MARK - Private Methods

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not a UI realm).
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executeTransaction(crossinline transaction: (Realm) -> T) {
        getAsyncRealm().use { it.executeTransaction { transaction(it) } }
    }

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not a UI realm) and returns the
     * result.
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executeTransactionAndReturn(crossinline transaction: (Realm) -> T): T {
        return getAsyncRealm().use {
            it.beginTransaction()
            return@use try {
                val result = transaction(it)
                it.commitTransaction()
                result
            } catch (e: Throwable) {
                when {
                    it.isInTransaction -> it.cancelTransaction()
                    else -> logw("Could not cancel transaction, not currently in one.")
                }
                throw e
            }

        }
    }

}