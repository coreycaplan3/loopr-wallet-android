package com.caplaninnovations.looprwallet.networking.prices

import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import kotlinx.coroutines.experimental.Deferred

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
            val environment = BuildConfig.ENVIRONMENT
            return when (environment) {
                "mocknet" -> CurrencyExchangeServiceMockImpl()
                "testnet", "mainnet" -> CurrencyExchangeServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }

    }

    fun getCurrentCurrencyExchangeRate(ticker: String): Deferred<CurrencyExchangeRate>

}