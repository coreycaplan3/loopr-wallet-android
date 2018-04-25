package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.*
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.markets.TradingPair
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
class LooprMarketsRepository : BaseRealmRepository(false) {

    override fun getAsyncRealm() = realmClient.getSharedInstance()

    /**
     * Toggles the *favorite* status of a trading pair
     */
    fun toggleIsFavorite(tradingPair: TradingPair) {
        runTransaction(Realm.Transaction {
            tradingPair.isFavorite = !tradingPair.isFavorite
            it.upsert(tradingPair)
        })
    }

    fun getMarkets(filter: MarketsFilter): LiveData<OrderedRealmCollection<TradingPair>> {
        // TODO apply filter
        return uiRealm.where<TradingPair>()
                .sort(TradingPair::primaryTicker)
                .findAllAsync()
                .asLiveData()
    }

}