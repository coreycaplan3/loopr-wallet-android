package org.loopring.looprwallet.hometransfers.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.loopr.LooprTransferRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey on 4/20/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: To retrieve this user's transfers of ETH, tokens, etc.
 *
 */
class ViewAllTransfersViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<LooprTransfer>, Unit>() {

    override val repository = LooprTransferRepository(currentWallet)

    fun getAllTransfers(owner: LifecycleOwner, onChange: (OrderedRealmCollection<LooprTransfer>) -> Unit) {
        initializeData(owner, Unit, onChange)
    }

    override fun getLiveDataFromRepository(parameter: Unit): LiveData<OrderedRealmCollection<LooprTransfer>> {
        return repository.getAllTransfers()
    }

    override fun getDataFromNetwork(parameter: Unit): Deferred<OrderedRealmCollection<LooprTransfer>> {
        TODO("Ethplorer get Address History") // TODO
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprTransfer>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: Unit): Boolean {
        return defaultIsRefreshNecessary()
    }

    override fun addSyncDataToRepository(parameter: Unit) {
        syncRepository.add(SyncData(syncType, null, Date()))
    }

    override val syncType: String = SyncData.SYNC_TYPE_TRANSFERS
}