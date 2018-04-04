package org.loopring.looprwallet.core.networking.ethplorer

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.cryptotokens.EthToken
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface EthplorerService {

    companion object {

        fun getInstance(): EthplorerService {
            val buildType = BuildUtility.BUILD_TYPE
            return when (buildType) {
                FLAVOR_MOCKNET -> EthplorerServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> EthplorerServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }

    }

    /**
     * Gets a token's information like price and total supply from the network.
     *
     * @param contractAddress The token's [EthToken.contractAddress] used to get the proper token's
     * information.
     */
    fun getTokenInfo(contractAddress: String): Deferred<EthToken>

    /**
     * Gets an address's information. This includes ETH and token balance information.
     */
    fun getAddressInfo(address: String): Deferred<List<EthToken>>

}