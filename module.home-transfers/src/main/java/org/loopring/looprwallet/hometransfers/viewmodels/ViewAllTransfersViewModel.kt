package org.loopring.looprwallet.hometransfers.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.ethplorer.EthplorerService
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
class ViewAllTransfersViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<LooprTransfer>, String>() {

    override val repository = LooprTransferRepository(currentWallet)

    private val ethplorerService by lazy {
        EthplorerService.getInstance()
    }

    fun getAllTransfers(owner: BaseFragment, address: String, onChange: (OrderedRealmCollection<LooprTransfer>) -> Unit) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<LooprTransfer>> {
        return repository.getAllTransfers()
    }

    override fun getDataFromNetwork(parameter: String): Deferred<OrderedRealmCollection<LooprTransfer>> {
        return ethplorerService.getAddressTransferHistory(parameter)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprTransfer>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: String): Boolean {
        return defaultIsRefreshNecessary(parameter)
    }

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    override val syncType: String = SyncData.SYNC_TYPE_TRANSFERS
}