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
interface LooprEtherScanService {

    companion object {

        fun getInstance(): LooprEtherScanService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                FLAVOR_MOCKNET -> LooprEtherScanServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> LooprEtherScanServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }

    }

}