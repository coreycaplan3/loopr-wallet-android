package com.caplaninnovations.looprwallet.networking.ethplorer

import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
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
            setTokenInfo(eth)

            val lrc = EthToken.LRC
            setTokenInfo(lrc)

            val appc = EthToken.APPC
            setTokenInfo(appc)

            val req = EthToken.REQ
            setTokenInfo(req)

            listOf(eth, lrc, appc, req)
        } else {
            throw IOException("No connection!")
        }
    }

    override fun getTokenInfo(contractAddress: String) = async {
        delay(500L)

        if (NetworkUtility.isNetworkAvailable()) {
            val tokenList = listOf(EthToken.ETH, EthToken.LRC, EthToken.APPC, EthToken.REQ)
            tokenList.forEach { setTokenInfo(it) }
            tokenList.firstOrNull { it.contractAddress == contractAddress }!!
        } else {
            throw IOException("No connection!")
        }
    }

    private fun setTokenInfo(token: CryptoToken) {
        when (token.identifier) {
            EthToken.ETH.identifier -> {
                token.priceInUsd = BigDecimal("5000.00")
                token.balance = BigDecimal("100")
            }
            EthToken.LRC.identifier -> {
                token.priceInUsd = BigDecimal("100.00")
                token.balance = BigDecimal("2500")
            }
            EthToken.APPC.identifier -> {
                token.priceInUsd = BigDecimal("500.00")
                token.balance = BigDecimal("0")
            }
            EthToken.REQ.identifier -> {
                token.priceInUsd = BigDecimal("900.00")
                token.balance = BigDecimal("2500")
            }
        }
    }

}