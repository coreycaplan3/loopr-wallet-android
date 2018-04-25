package org.loopring.looprwallet.order.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: A view-model for showing a single [LooprOrder] based on its order hash.
 *
 */
class OrderSummaryViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<LooprOrder, String>() {

    override val repository = LooprOrderRepository(currentWallet)

    private val service = LooprOrderService.getInstance()

    fun getOrderByHash(owner: LifecycleOwner, orderHash: String, onChange: (LooprOrder) -> Unit) {
        initializeData(owner, orderHash, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<LooprOrder> {
        return repository.getOrderByHash(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<LooprOrder> {
        return service.getOrderByHash(parameter)
    }

    override fun addNetworkDataToRepository(data: LooprOrder) {
        repository.add(data)
    }

    override fun isRefreshNecessary(parameter: String): Boolean {
        val time = data?.lastUpdated?.time ?: return true

        return isRefreshNecessaryBasedOnDate(time)
    }

    override fun addSyncDataToRepository(parameter: String) {
        // NO OP - We use the item itself instead of the sync repository
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_NONE
}