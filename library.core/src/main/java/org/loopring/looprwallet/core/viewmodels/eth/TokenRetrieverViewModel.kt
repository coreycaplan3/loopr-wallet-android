package org.loopring.looprwallet.core.viewmodels.eth

import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.etherscan.EtherScanService
import com.caplaninnovations.looprwallet.networking.ethplorer.EthplorerService
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import kotlinx.coroutines.experimental.async

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Responsible for getting a token from the [EtherScanService] and adding it to
 * the application for continual use.
 */
class TokenRetrieverViewModel(currentWallet: LooprWallet) : TransactionViewModel<EthToken>() {

    val repository = BaseRealmRepository(currentWallet)

    val ethplorerService = EthplorerService.getInstance()
    val etherScanService = EtherScanService.getInstance()

    /**
     * Gets a token's info (including its binary) from the network and saves it to the shared Realm.
     *
     * This
     */
    fun getTokenInfoFromNetworkAndAdd(contractAddress: String) = async {
        isTransactionRunning.postValue(true)
        try {
            val token = ethplorerService.getTokenInfo(contractAddress).await()
            token.binary = etherScanService.getTokenBinary(contractAddress).await()
            repository.add(token)
            true
        } catch (e: Throwable) {
            error.postValue(e)
            false
        } finally {
            isTransactionRunning.postValue(false)
        }
    }

}