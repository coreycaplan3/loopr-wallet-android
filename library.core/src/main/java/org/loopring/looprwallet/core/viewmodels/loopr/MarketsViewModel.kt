package org.loopring.looprwallet.core.viewmodels.loopr

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_MARKETS
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService.Companion.KEY_HAS_SYNCED_MARKETS
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService.Companion.SYNC_CONTEXT
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
class MarketsViewModel : OfflineFirstViewModel<OrderedRealmCollection<TradingPair>, TradingPairFilter>() {

    override val repository = LooprMarketsRepository()

    private val service by lazy {
        LooprMarketsService.getInstance()
    }

    private val settings by lazy {
        LooprSettings.getInstance(CoreLooprWalletApp.context)
    }

    private lateinit var sortBy: String

    private var newSortBy: String? = null

    fun getHomeMarkets(
            owner: ViewLifecycleFragment,
            filter: TradingPairFilter,
            sortBy: String,
            onChange: (OrderedRealmCollection<TradingPair>) -> Unit
    ) {
        this.sortBy = sortBy
        initializeData(owner, filter, onChange)
    }

    fun onSortByChange(newSortBy: String) {
        this.newSortBy = newSortBy
        refresh()
    }

    override fun getLiveDataFromRepository(parameter: TradingPairFilter): LiveData<OrderedRealmCollection<TradingPair>> {
        return repository.getMarkets(parameter, newSortBy ?: sortBy)
    }

    override fun getDataFromNetwork(parameter: TradingPairFilter): Deferred<OrderedRealmCollection<TradingPair>> {
        when {
            !settings.getBoolean(KEY_HAS_SYNCED_MARKETS, false) -> runBlocking {
                CoreLooprWalletApp.awaitInitialSync()
                settings.putBoolean(KEY_HAS_SYNCED_MARKETS, true)
            }
            else -> service.syncSupportedMarkets().also { deferred ->
                async(SYNC_CONTEXT) {
                    deferred.await()
                    logi("Finished sync job…")
                }
            }
        }

        return service.getMarkets()
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<TradingPair>, parameter: TradingPairFilter) {
        repository.insertMarkets(data as RealmList<TradingPair>)
    }

    // Default to returning true for now
    override fun isRefreshNecessary(parameter: TradingPairFilter): Boolean {
        if (sortBy != newSortBy) {
            newSortBy = sortBy
            return true
        }

        return defaultIsRefreshNecessary()
    }

    override fun addSyncDataToRepository(parameter: TradingPairFilter) {
        syncRepository.add(SyncData(syncType, null, Date()))
    }

    override val syncType = SYNC_TYPE_MARKETS
}