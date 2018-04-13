package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprMarketsRepository : BaseRealmRepository() {

    override fun getRealm() = realmClient.getSharedInstance()

    fun getMarkets(filter: MarketsFilter): LiveData<RealmResults<RealmModel>> {
        TODO("TODO")
    }

    fun getMarketDetails(tradePair: Any) {
        TODO("TODO")
    }

}