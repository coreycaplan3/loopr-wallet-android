package org.loopring.looprwallet.tradedetails.repositories

import android.arch.lifecycle.LiveData
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairDetailsRepository : BaseRealmRepository() {

    fun doesTradingPairExist(market: String, context: HandlerContext = IO) = async(context) {
        getRealmFromContext(context)
                .where<TradingPair>()
                .equalTo(TradingPair::market, market)
                .findFirst() != null
    }

    fun getTradingPairByMarket(primaryTicker: String, secondaryTicker: String, context: HandlerContext = UI): LiveData<TradingPair> {
        return getRealmFromContext(context)
                .where<TradingPair>()
                .equalTo(TradingPair::primaryTicker, primaryTicker)
                .equalTo(TradingPair::secondaryTicker, secondaryTicker)
                .findFirstAsync()
                .asLiveData()
    }

}