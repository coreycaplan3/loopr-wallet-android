package org.loopring.looprwallet.core.viewmodels.eth

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.eth.EthereumGenericService
import org.loopring.looprwallet.core.repositories.eth.EthereumBlockNumberRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum
import java.util.*

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 *
 */
class EthereumBlockNumberViewModel : StreamingViewModel<EthBlockNum, Unit>() {

    /**
     * 24 seconds in millis
     */
    override val waitTime: Long
        get() = 24 * 1000L

    override val repository = EthereumBlockNumberRepository()

    private val ethereumService by lazy {
        EthereumGenericService.getInstance()
    }

    fun getEthereumBlockNumber(owner: ViewLifecycleFragment, onChange: (EthBlockNum) -> Unit) {
        initializeData(owner, Unit, onChange)
    }

    override fun getLiveDataFromRepository(parameter: Unit): LiveData<EthBlockNum> {
        return repository.getEthereumBlockNumber()
    }

    override fun getDataFromNetwork(parameter: Unit): Deferred<EthBlockNum> {
        return ethereumService.getBlockNumber()
    }

    override fun addNetworkDataToRepository(data: EthBlockNum, parameter: Unit) {
        repository.add(data)
    }

    override fun isRefreshNecessary(parameter: Unit): Boolean {
        return defaultIsRefreshNecessary()
    }

    override fun addSyncDataToRepository(parameter: Unit) {
        return syncRepository.add(SyncData(SyncData.SYNC_TYPE_ETHEREUM_BLOCK_NUMBER, null, Date()))
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_ETHEREUM_BLOCK_NUMBER
}