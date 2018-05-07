package org.loopring.looprwallet.core.repositories

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.deleteFromRealm
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.extensions.upsertCopyToRealm
import org.loopring.looprwallet.core.models.android.architecture.IO
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
     * This realm can **ONLY** be used from the UI thread
     */
    protected val uiRealm: Realm

    /**
     * This realm can **ONLY** be accessed from the IO thread
     */
    protected val ioRealm: Realm

    init {
        coreLooprComponent.inject(this)

        when (isPrivateInstance) {
            true -> {
                uiRealm = CoreLooprWalletApp.uiPrivateRealm
                ioRealm = CoreLooprWalletApp.asyncPrivateRealm
            }
            else -> {
                uiRealm = CoreLooprWalletApp.uiSharedRealm
                ioRealm = CoreLooprWalletApp.asyncSharedRealm
            }
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

    fun runTransaction(transaction: Realm.Transaction, context: HandlerContext = IO) {
        getRealmFromContext(context).executeTransaction(transaction)
    }

    final override fun add(data: RealmModel, context: HandlerContext) {
        getRealmFromContext(context).executeTransaction { it.upsert(data) }
    }

    final override fun addList(data: List<RealmModel>, context: HandlerContext) {
        getRealmFromContext(context).executeTransaction { it.upsert(data) }
    }

    final override fun remove(data: RealmModel, context: HandlerContext) {
        getRealmFromContext(context).executeTransaction { data.deleteFromRealm() }
    }

    final override fun remove(data: List<RealmModel>, context: HandlerContext) {
        if (data is RealmResults) {
            getRealmFromContext(context).executeTransaction { data.deleteAllFromRealm() }
        }
    }

    // MARK - Protected Methods

    protected fun getRealmFromContext(context: HandlerContext) = when(context) {
        UI -> uiRealm
        IO -> ioRealm
        else -> throw IllegalArgumentException("Invalid context, found: $context")
    }

    // MARK - Private Methods

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not a UI realm) and returns the
     * result.
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executeTransactionAndReturn(crossinline transaction: (Realm) -> T): T {
        ioRealm.beginTransaction()
        return try {
            val result = transaction(ioRealm)
            ioRealm.commitTransaction()
            result
        } catch (e: Throwable) {
            when {
                ioRealm.isInTransaction -> ioRealm.cancelTransaction()
                else -> logw("Could not cancel transaction, not currently in one.")
            }
            throw e
        }
    }

}