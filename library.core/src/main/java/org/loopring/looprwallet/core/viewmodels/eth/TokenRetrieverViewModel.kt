package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.networking.etherscan.LooprEtherScanService
import org.loopring.looprwallet.core.networking.ethplorer.LooprEthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Responsible for getting a token from the [LooprEtherScanService] and adding it to
 * the application for continual use.
 */
class TokenRetrieverViewModel : TransactionViewModel<LooprToken>() {

    val repository = EthTokenRepository()

    private val ethplorerService = LooprEthplorerService.getInstance()
    private val etherScanService = LooprEtherScanService.getInstance()

    /**
     * Gets a token's info (including its binary) from the network and saves it to the shared Realm,
     * if the token does **not** already exist.
     *
     * @return True if the token was added successfully or false otherwise.
     */
    fun getTokenInfoFromNetworkAndAdd(contractAddress: String) = async(IO) {
        mIsTransactionRunning.postValue(true)
        return@async try {
            val token = ethplorerService.getTokenInfo(contractAddress).await()
            token.binary = etherScanService.getTokenBinary(contractAddress).await()

            if (repository.getTokenByContractAddress(token.identifier) == null) {
                // The token does NOT already exist so we can safely add it
                repository.add(token)
            }

            mResult.postValue(token)

            true
        } catch (e: Throwable) {
            mError.postValue(e)
            false
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}