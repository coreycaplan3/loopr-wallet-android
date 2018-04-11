package org.loopring.looprwallet.core.viewmodels

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.sync.SyncRepository

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An instance of [OfflineFirstViewModel] that doesn't ping the network ever.
 * Instead, it focuses **only** on loading data from the device's repository.
 */
abstract class OfflineOnlyViewModel<T, U> : OfflineFirstViewModel<T, U>() {

    override val syncType = throw NotImplementedError("Not needed for offline-only ViewModels")

    override fun addNetworkDataToRepository(data: T) {
        throw NotImplementedError("This method should never be called")
    }

    override fun getDataFromNetwork(parameter: U): Deferred<T> {
        throw NotImplementedError("This method should never be called")
    }

    /**
     * We never need to refresh since the data is ALWAYS offline
     */
    override fun isRefreshNecessary(parameter: U) = false

    override fun addSyncDataToRepository(parameter: U) {
        throw NotImplementedError("This method should never be called")
    }

}