package com.caplaninnovations.looprwallet.networking.prices

import com.caplaninnovations.looprwallet.BuildConfig

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To retrieve market information about a token.
 *
 */
interface TokenPriceService {

    companion object {

        /**
         * Gets an instance of the transaction repository for executing ether transactions
         */
        fun getInstance(): TokenPriceService {
            val environment = BuildConfig.ENVIRONMENT
            return when (environment) {
                "mocknet" -> TokenPriceServiceMockImpl()
                "testnet", "mainnet" -> TokenPriceServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }


    }

}