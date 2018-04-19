package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.networking.etherscan.EtherScanService
import org.loopring.looprwallet.core.networking.ethplorer.EthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Responsible for getting a token from the [EtherScanService] and adding it to
 * the application for continual use.
 */
class TokenRetrieverViewModel : TransactionViewModel<EthToken>() {

    val repository = EthTokenRepository()

    private val ethplorerService = EthplorerService.getInstance()
    private val etherScanService = EtherScanService.getInstance()

    /**
     * Gets a token's info (including its binary) from the network and saves it to the shared Realm.
     *
     * This
     */
    fun getTokenInfoFromNetworkAndAdd(contractAddress: String) = async {
        mIsTransactionRunning.postValue(true)
        try {
            val token = ethplorerService.getTokenInfo(contractAddress).await()
            token.binary = etherScanService.getTokenBinary(contractAddress).await()
            repository.add(token)
            true
        } catch (e: Throwable) {
            mError.postValue(e)
            false
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}