package com.caplaninnovations.looprwallet.repositories

import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.removeAllListenersAndClose
import com.caplaninnovations.looprwallet.extensions.upsert
import com.caplaninnovations.looprwallet.extensions.upsertCopyToRealm
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.deleteFromRealm

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To persist information to a [Realm] and allow queries to be written against
 * [Realm] as well.
 *
 */
open class BaseRealmRepository(private val currentWallet: LooprWallet)
    : BaseRepository<RealmModel> {

    private val realmClient = LooprWalletApp.dagger.realmClient

    /**
     * A realm instance that can only be accessed from the main thread.
     */
    val uiRealm = realmClient.getInstance(currentWallet.walletName, currentWallet.realmKey)

    /**
     * ** THIS METHOD MUST BE CALLED FROM THE MAIN THREAD**
     *
     * Copies [data] to the [Realm] and returns it.
     *
     * @param data The data to copy into the [Realm]
     */
    fun <T : RealmModel> addAndReturn(data: T): T {
        executeRealmTransaction { it.upsertCopyToRealm(data) }
        return data
    }

    final override fun add(data: RealmModel) {
        executeRealmTransaction { it.upsert(data) }
    }

    final override fun addList(data: List<RealmModel>) {
        executeRealmTransaction { it.upsert(data) }
    }

    final override fun removeData(data: RealmModel) {
        executeRealmTransaction { data.deleteFromRealm() }
    }

    final override fun removeData(data: List<RealmModel>) {
        if (data is RealmResults) {
            executeRealmTransaction { data.deleteAllFromRealm() }
        }
    }

    final override fun clear() {
        uiRealm.removeAllListenersAndClose()
    }

    // MARK - Private Methods

    /**
     * Executes a realm transaction with a one-time-use [Realm] (not [uiRealm]).
     *
     * @param transaction A function that is executed from **within** a [Realm] transaction
     */
    private inline fun <T> executeRealmTransaction(crossinline transaction: (Realm) -> T) {
        val realm = realmClient.getInstance(currentWallet.walletName, currentWallet.realmKey)
        realm.executeTransaction { transaction(it) }
        realm.close()
    }

}