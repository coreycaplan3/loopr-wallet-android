package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.networking.etherscan.LooprEtherScanService
import org.loopring.looprwallet.core.networking.ethplorer.LooprEthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.utilities.LooprTokenUtility
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Responsible for getting a single token from the [LooprEtherScanService] and
 * adding it to the application for continual use.
 */
class InitialTokenAdderViewModel : TransactionViewModel<LooprToken>() {

    val repository = EthTokenRepository()

    private val ethplorerService = LooprEthplorerService.getInstance()

    /**
     * Gets a token's info (including its binary) from the network and saves it to the shared Realm,
     * if the token does **not** already exist.
     *
     * @param contractAddress The address of the token
     * @param address The address of the user adding the token
     * @return True if the token was added successfully or false otherwise.
     */
    fun getTokenInfoFromNetworkAndAdd(contractAddress: String, address: String?) = async(IO) {
        mIsTransactionRunning.postValue(true)
        return@async try {
            val newToken = ethplorerService.getTokenInfo(contractAddress).await()
            val oldToken = repository.getTokenByContractAddressNow(newToken.identifier)

            val tokenToInsert = LooprTokenUtility.mapTokenToBalanceAndReturnToken(newToken, oldToken, address)

            repository.add(tokenToInsert)
            mResult.postValue(tokenToInsert)
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}