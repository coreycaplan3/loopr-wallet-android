package org.loopring.looprwallet.core.repositories

import io.realm.*
import io.realm.kotlin.deleteFromRealm
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.android.architecture.IO

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To persist information to a [Realm] and allow queries to be written against
 * [Realm] as well.
 */
abstract class BaseRealmRepository : BaseRepository<RealmModel> {

    /**
     * This realm can **ONLY** be used from the UI thread
     */
    private val uiRealm: Realm = CoreLooprWalletApp.uiGlobalRealm

    /**
     * This realm can **ONLY** be accessed from the IO thread
     */
    private val ioRealm: Realm = CoreLooprWalletApp.asyncGlobalRealm

    /**
     * Runs a given transaction on the provided [context]'s [Realm] instance.
     *
     * ** This method is called assuming it's on the proper thread!**
     */
    fun runTransaction(context: HandlerContext = IO, transaction: (Realm) -> Unit) {
        getRealmFromContext(context).executeTransaction(transaction)
    }

    /**
     * Runs a given *add transaction* on the provided [context]'s [Realm] instance.
     *
     * ** This method is called assuming it's on the proper thread!**
     */
    final override fun add(data: RealmModel, context: HandlerContext) {
        getRealmFromContext(context)
                .executeTransaction { it.upsert(data) }
    }

    /**
     * Runs a given *add list transaction* on the provided [context]'s [Realm] instance.
     *
     * ** This method is called assuming it's on the proper thread!**
     */
    final override fun addList(data: List<RealmModel>, context: HandlerContext) {
        getRealmFromContext(context)
                .executeTransaction { it.upsert(data) }
    }

    /**
     * Runs a given *remove transaction* on the provided [context]'s [Realm] instance.
     *
     * ** This method is called assuming it's on the proper thread!**
     */
    final override fun remove(data: RealmModel, context: HandlerContext) {
        getRealmFromContext(context)
                .executeTransaction { data.deleteFromRealm() }
    }

    /**
     * Runs a given *remove list transaction* on the provided [context]'s [Realm] instance.
     *
     * ** This method is called assuming it's on the proper thread!**
     */
    final override fun remove(data: List<RealmModel>, context: HandlerContext) {
        val realm = getRealmFromContext(context)
        when (data) {
            is OrderedRealmCollection -> realm.executeTransaction { data.deleteAllFromRealm() }
            is RealmResults -> realm.executeTransaction { data.deleteAllFromRealm() }
            is RealmList -> realm.executeTransaction { data.deleteAllFromRealm() }
            else -> loge("Invalid list type, found: ${data::class.java.simpleName}", IllegalArgumentException())
        }
    }

    // MARK - Protected Methods

    /**
     * Gets a given [Realm] instance, based on the [HandlerContext] passed as an argument.
     * @return A [Realm] that can be used for reading/writing to the mobile database.
     */
    protected fun getRealmFromContext(context: HandlerContext) = when (context) {
        UI -> uiRealm
        IO -> ioRealm
        else -> throw IllegalArgumentException("Invalid context, found: $context")
    }

}