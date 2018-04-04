package org.loopring.looprwallet.core.networking.prices

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface CurrencyExchangeService {

    companion object {

        /**
         * Gets an instance of the transaction repository for executing ether transactions
         */
        fun getInstance(): CurrencyExchangeService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                FLAVOR_MOCKNET -> CurrencyExchangeServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> CurrencyExchangeServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }

    }

    /**
     * Gets the given [currency]'s exchange rate from the network.
     *
     * @param currency The currency whose exchange rate needs to be retrieved.
     */
    fun getCurrentCurrencyExchangeRate(currency: String): Deferred<CurrencyExchangeRate>

}