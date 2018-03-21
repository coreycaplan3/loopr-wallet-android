package com.caplaninnovations.looprwallet.networking.ethplorer

import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.utilities.BuildUtility.FLAVOR_MAINNET
import com.caplaninnovations.looprwallet.utilities.BuildUtility.FLAVOR_MOCKNET
import com.caplaninnovations.looprwallet.utilities.BuildUtility.FLAVOR_TESTNET
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface EthplorerApiService {

    companion object {

        fun getInstance(): EthplorerApiService {
            val buildType = BuildConfig.FLAVOR
            return when (buildType) {
                FLAVOR_MOCKNET -> EthplorerApiServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> EthplorerApiServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }

    }

    fun getTokenInfo(contractAddress: String): Deferred<EthToken>

    /**
     * Gets an address's information. This includes ETH and token balance information.
     */
    fun getAddressInfo(address: String): Deferred<List<EthToken>>

}