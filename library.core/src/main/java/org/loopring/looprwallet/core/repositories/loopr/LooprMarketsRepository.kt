package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
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

    /**
     * Toggles the *favorite* status of a trading pair
     *
     * @param market The *primaryTicker-secondaryTicker* or primary key of the [TradingPair]
     */
    fun toggleIsFavorite(market: String, context: HandlerContext = IO) = async(context) {
        runTransaction(context) { realm ->
            realm.where<TradingPair>()
                    .equalTo(TradingPair::market, market)
                    .findFirst()
                    ?.let {
                        it.isFavorite = !it.isFavorite
                        realm.upsert(it)
                    }
        }
    }

    fun insertMarkets(list: RealmList<TradingPair>, context: HandlerContext = IO) = getRealmFromContext(context).executeTransaction { realm ->
        val allTradingPairs = realm.where<TradingPair>().findAll()
        list.map { newTradingPair ->
            // We need to copy over the old information to the new ticker before insertion
            return@map allTradingPairs.where()
                    .equalTo(TradingPair::market, newTradingPair.market)
                    .findFirst()
                    ?.also { oldTradingPair ->
                        // Copy over the old ticker's information to the new ticker
                        newTradingPair.primaryToken = oldTradingPair.primaryToken
                        newTradingPair.secondaryToken = oldTradingPair.secondaryToken
                        newTradingPair.isFavorite = oldTradingPair.isFavorite
                    } ?: newTradingPair

        }.forEach { tradingPair ->
            realm.upsert(tradingPair)
        }
    }

    fun getMarketNow(market: String, context: HandlerContext = IO): TradingPair? {
        val tradingPair = getRealmFromContext(context)
                .where<TradingPair>()
                .equalTo(TradingPair::market, market, Case.INSENSITIVE)
                .findFirst()

        return tradingPair?.let { getRealmFromContext(context).copyFromRealm(it) }
    }

    fun getMarkets(filter: TradingPairFilter, context: HandlerContext = UI): LiveData<OrderedRealmCollection<TradingPair>> {
        // TODO apply filter; look into making these async queries run on a background thread too
        return getRealmFromContext(context)
                .where<TradingPair>()
                .apply {
                    equalTo(TradingPair::isFavorite, filter.isFavorites)

                    when (filter.sortBy) {
                        TradingPairFilter.SORT_BY_TICKER_ASC -> sort(TradingPair::primaryTicker, Sort.ASCENDING)
                        TradingPairFilter.SORT_BY_PERCENTAGE_CHANGE_ASC -> sort(TradingPair::primaryTicker, Sort.ASCENDING)
                        TradingPairFilter.SORT_BY_PERCENTAGE_CHANGE_DESC -> sort(TradingPair::primaryTicker, Sort.DESCENDING)
                    }

                }
                .findAllAsync()
                .asLiveData()
    }

}