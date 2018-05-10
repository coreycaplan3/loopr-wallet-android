package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum

/**
 * Created by corey on 5/9/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class EthereumGenericServiceProdImpl: EthereumGenericService() {

    override fun getBlockNumber(): Deferred<EthBlockNum> {
        TODO("yea...")
    }

}