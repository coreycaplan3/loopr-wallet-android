package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.blockchain.EthereumBlockNumber
import java.math.BigInteger

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: For performing generic Ethereum operations, like viewing the block number
 *
 */
interface EthereumService {

    companion object {

        fun getInstance(): EthereumService {
            //TODO
            return EthereumServiceMockImpl()
        }

    }

    /**
     * @return The current block number of the Ethereum blockchain
     */
    fun getBlockNumber(): Deferred<EthereumBlockNumber>

}