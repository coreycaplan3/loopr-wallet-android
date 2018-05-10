package org.loopring.looprwallet.core.networking.etherscan

import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprEtherScanServiceProdImpl: LooprEtherScanService {

    override fun getTokenBinary(contractAddress: String): Deferred<String> {
//        return EtherscanService.getService().getTransactions(contractAddress).await()
        TODO("...")
    }

}