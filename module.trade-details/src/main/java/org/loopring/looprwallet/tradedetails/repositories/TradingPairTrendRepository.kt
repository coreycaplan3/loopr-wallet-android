package org.loopring.looprwallet.tradedetails.repositories

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend
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

    override fun getRealm() = realmClient.getSharedInstance()

    fun getTradingPairTrend(filter: TradingPairFilter): LiveData<OrderedRealmCollection<TradingPairTrend>> {
        return uiRealm.where<TradingPairTrend>()
                .equalTo(TradingPairTrend::tradingPair, filter.market)
                .equalTo(TradingPairTrend::graphDateFilter, filter.graphDateFilter)
                .sort(TradingPairTrend::startDate)
                .findAllAsync()
                .asLiveData()
    }

}