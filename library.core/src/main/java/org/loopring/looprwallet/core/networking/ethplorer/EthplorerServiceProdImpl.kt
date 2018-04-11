package org.loopring.looprwallet.core.networking.ethplorer

import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthplorerServiceProdImpl: EthplorerService {

    override fun getAddressInfo(address: String): Deferred<List<EthToken>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTokenInfo(contractAddress: String): Deferred<EthToken> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}