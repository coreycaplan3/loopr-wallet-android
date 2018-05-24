package org.loopring.looprwallet.core.repositories.markets

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairTrendRepository : BaseRealmRepository() {

    fun getTradingPairTrend(filter: TradingPairGraphFilter, context: HandlerContext = UI): LiveData<OrderedRealmCollection<TradingPairTrend>> {
        return getRealmFromContext(context)
                .where<TradingPairTrend>()
                .equalTo(TradingPairTrend::tradingPair, filter.market)
                .equalTo(TradingPairTrend::graphDateFilter, filter.graphDateFilter)
                .sort(TradingPairTrend::startDate)
                .findAllAsync()
                .asLiveData()
    }

}