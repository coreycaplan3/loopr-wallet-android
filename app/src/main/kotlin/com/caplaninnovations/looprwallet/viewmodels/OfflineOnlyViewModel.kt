package com.caplaninnovations.looprwallet.viewmodels

import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.sync.SyncRepository
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An instance of [OfflineFirstViewModel] that doesn't ping the network ever.
 * Instead, it focuses **only** on loading data from the device's repository.
 */
abstract class OfflineOnlyViewModel<T, U>(currentWallet: LooprWallet) : OfflineFirstViewModel<T, U>() {

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    override val syncType = throw NotImplementedError("Not needed for offline-only ViewModels")

    override fun addNetworkDataToRepository(data: T) {
        throw NotImplementedError("This method should never be called")
    }

    override fun getDataFromNetwork(parameter: U): Deferred<T> {
        throw NotImplementedError("This method should never be called")
    }

    override fun isRefreshNecessary(parameter: U) = false

}