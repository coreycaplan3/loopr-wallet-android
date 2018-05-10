package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: For performing generic Ethereum operations that don't require credentials,
 * like viewing the block number
 */
abstract class EthereumGenericService {

    companion object {

        fun getInstance(): EthereumGenericService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                BuildUtility.FLAVOR_MOCKNET -> EthereumGenericServiceMockImpl()
                BuildUtility.FLAVOR_TESTNET, BuildUtility.FLAVOR_MAINNET -> EthereumGenericServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }

    /**
     * @return The current block number of the Ethereum blockchain
     */
    abstract fun getBlockNumber(): Deferred<EthBlockNum>

}