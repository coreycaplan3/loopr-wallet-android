package org.loopring.looprwallet.core.networking.etherscan

import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwalletnetwork.services.EtherscanService

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprEtherScanServiceProdImpl: LooprEtherScanService {

    init {
        EtherscanService.apiKey = BuildConfig.ETHERSCAN_API_KEY
    }

}