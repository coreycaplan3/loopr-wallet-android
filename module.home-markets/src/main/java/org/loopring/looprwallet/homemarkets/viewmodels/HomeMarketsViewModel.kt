package org.loopring.looprwallet.homemarkets.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.RealmModel
import io.realm.RealmResults
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_MARKETS
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
class HomeMarketsViewModel : OfflineFirstViewModel<List<RealmModel>, MarketsFilter>() {

    override val repository = LooprMarketsRepository()

    @Suppress("UNCHECKED_CAST")
    fun getAllHomeMarkets(
            owner: LifecycleOwner,
            filter: MarketsFilter,
            onChange: (RealmResults<RealmModel>) -> Unit
    ) {
        initializeData(owner, filter, onChange as (List<RealmModel>) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getLiveDataFromRepository(parameter: MarketsFilter): LiveData<List<RealmModel>> {
        return repository.getMarkets(parameter) as LiveData<List<RealmModel>>
    }

    override fun getDataFromNetwork(parameter: MarketsFilter): Deferred<List<RealmModel>> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: List<RealmModel>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: MarketsFilter) = defaultIsRefreshNecessary()

    override fun addSyncDataToRepository(parameter: MarketsFilter) {
        repository.add(SyncData(syncType, null, Date()))
    }

    override val syncType = SYNC_TYPE_MARKETS
}