package org.loopring.looprwallet.core.networking.etherscan

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class for interacting with Etherscan related methods
 *
 */
interface EtherScanService {

    companion object {
        fun getInstance(): EtherScanService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                FLAVOR_MOCKNET -> EtherScanServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> EtherScanServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }

    /**
     * Gets a token's binary from the Etherscan service
     */
    fun getTokenBinary(contractAddress: String): Deferred<String>

}