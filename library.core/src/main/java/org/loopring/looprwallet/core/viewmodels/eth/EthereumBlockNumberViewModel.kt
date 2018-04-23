package org.loopring.looprwallet.core.viewmodels.eth

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.blockchain.EthereumBlockNumber
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.repositories.eth.EthereumBlockNumberRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
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
class EthereumBlockNumberViewModel : StreamingViewModel<EthereumBlockNumber, Unit>() {

    /**
     * 24 seconds in millis
     */
    override val waitTime: Long
        get() = 24 * 1000L

    override val repository = EthereumBlockNumberRepository()

    fun getEthereumBlockNumber(owner: LifecycleOwner, onChange: (EthereumBlockNumber) -> Unit) {
        initializeData(owner, Unit, onChange)
    }

    override fun getLiveDataFromRepository(parameter: Unit): LiveData<EthereumBlockNumber> {
        return repository.getEthereumBlockNumber()
    }

    override fun getDataFromNetwork(parameter: Unit): Deferred<EthereumBlockNumber> {
        TODO("not implemented") // TODO
    }

    override fun addNetworkDataToRepository(data: EthereumBlockNumber) {
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