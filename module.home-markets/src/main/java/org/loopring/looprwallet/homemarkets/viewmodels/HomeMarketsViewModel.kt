package org.loopring.looprwallet.homemarkets.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_MARKETS
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey Caplan on 4/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeMarketsViewModel : OfflineFirstViewModel<OrderedRealmCollection<TradingPair>, TradingPairFilter>() {

    override val repository = LooprMarketsRepository()

    private val service by lazy {
        LooprMarketsService.getInstance()
    }

    fun getHomeMarkets(
            owner: ViewLifecycleFragment,
            filter: TradingPairFilter,
            onChange: (OrderedRealmCollection<TradingPair>) -> Unit
    ) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: TradingPairFilter): LiveData<OrderedRealmCollection<TradingPair>> {
        return repository.getMarkets(parameter)
    }

    override fun getDataFromNetwork(parameter: TradingPairFilter): Deferred<OrderedRealmCollection<TradingPair>> {
        return service.getMarkets()
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<TradingPair>, parameter: TradingPairFilter) {
        repository.insertMarkets(data as RealmList<TradingPair>)
    }

    override fun isRefreshNecessary(parameter: TradingPairFilter) = defaultIsRefreshNecessary()

    override fun addSyncDataToRepository(parameter: TradingPairFilter) {
        syncRepository.add(SyncData(syncType, null, Date()))
    }

    override val syncType = SYNC_TYPE_MARKETS
}