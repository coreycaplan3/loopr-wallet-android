package com.caplaninnovations.looprwallet.networking.ethplorer

import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.utilities.NetworkUtility
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.io.IOException
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthplorerApiServiceMockImpl : EthplorerApiService {

    override fun getAddressInfo(address: String) = async {
        delay(1000L)

        if (NetworkUtility.isNetworkAvailable()) {
            val eth = EthToken.ETH
            eth.balance = BigDecimal("100")
            eth.priceInUsd = BigDecimal("1000.45")

            val lrc = EthToken.LRC
            lrc.balance = BigDecimal("25000")
            lrc.priceInUsd = BigDecimal("98.12")

            val appc = EthToken.APPC
            appc.balance = BigDecimal("25000")
            appc.priceInUsd = BigDecimal("400.00")

            val req = EthToken.REQ
            req.balance = BigDecimal("25000")
            req.priceInUsd = BigDecimal("100.38")

            listOf(eth, lrc, appc, req)
        } else {
            throw IOException("No connection!")
        }
    }

    override fun getTokenInfo(contractAddress: String) = async {
        delay(5000L)

        if (NetworkUtility.isNetworkAvailable()) {
            val token = EthToken.ETH
            token.priceInUsd = BigDecimal("1000.45")
            token.priceInNativeCurrency = BigDecimal("1000.45")
            token
        } else {
            throw IOException("No connection!")
        }
    }

}