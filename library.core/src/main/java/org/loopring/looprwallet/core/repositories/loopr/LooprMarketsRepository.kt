package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.markets.TradingPair
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

    /**
     * Toggles the *favorite* status of a trading pair
     *
     * @param market The *primaryTicker-secondaryTicker* or primary key of the [TradingPair]
     */
    fun toggleIsFavorite(market: String) = async(IO) {
        runTransaction(Realm.Transaction { realm ->
            realm.where<TradingPair>().equalTo(TradingPair::market, market).findFirst()?.let {
                it.isFavorite = !it.isFavorite
                realm.upsert(it)
            }
        })
    }

    fun insertMarkets(list: RealmList<TradingPair>) {
        ioRealm.executeTransaction { realm ->
            list.forEach {
                realm.upsert(it.primaryToken)
            }
            realm.upsert(list)
        }
    }

    fun getMarkets(filter: MarketsFilter): LiveData<OrderedRealmCollection<TradingPair>> {
        // TODO apply filter; look into making these async queries run on a background thread too
        return uiRealm.where<TradingPair>()
                .sort(TradingPair::primaryTicker)
                .findAllAsync()
                .asLiveData()
    }

}