package org.loopring.looprwallet.createorder.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import io.realm.OrderedRealmCollection
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import java.math.BigDecimal

/**
 * Created by corey on 5/29/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A [ViewModel] for passing special data between fragments in this activity.
 */
class CreateOrderViewModel : ViewModel() {

    /**
     * The custom entered price to be passed from the *EnterPriceFragment* to the
     * *CreateOrderFragment*
     */
    val priceLiveData: MutableLiveData<BigDecimal> = MutableLiveData()

    /**
     * The currently selected [TradingPair] for passing data between the *CreateOrderFragment*,
     * *EnterPriceFragment*, and *ConfirmOrderFragment*
     */
    val tradingPair: LiveData<TradingPair> = MutableLiveData()

    /**
     * The [LooprToken] that represents the LRC token and its price information
     */
    val lrcWethTradingPair: LiveData<TradingPair> = MutableLiveData()

    /**
     * The [AppLooprOrder] for passing between thee *CreateOrderFragment*, *ConfirmOrderFragment*,
     * and the *OrderPlacedFragment*.
     */
    val order: LiveData<AppLooprOrder> = MutableLiveData()

    private val marketsLiveData: LiveData<OrderedRealmCollection<TradingPair>>

    private val repository = LooprMarketsRepository()

    private val marketsObserver by lazy {
        Observer<OrderedRealmCollection<TradingPair>> {
            if (it == null) {
                return@Observer
            }

            val list = it.createSnapshot()
            val lrcWethMarket = "${LooprToken.LRC.ticker}-${LooprToken.WETH.ticker}"

            val index = list.binarySearch(TradingPair(lrcWethMarket), Comparator { m1, m2 ->
                m1.market.compareTo(m2.market)
            })

            if (index >= 0) {
                (this.lrcWethTradingPair as MutableLiveData).value = list[index]
            }

        }
    }

    init {
        val filter = TradingPairFilter(false, TradingPairFilter.CHANGE_PERIOD_1D)
        marketsLiveData = repository.getMarkets(filter, TradingPairFilter.SORT_BY_TICKER_ASC)
    }

    override fun onCleared() {
        super.onCleared()

        marketsLiveData.removeObserver(marketsObserver)
    }

}