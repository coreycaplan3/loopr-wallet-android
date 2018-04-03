package org.loopring.looprwallet.core.networking.ethplorer

import org.loopring.looprwallet.core.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.cryptotokens.EthToken
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwallet.core.utilities.NetworkUtility.MOCK_SERVICE_CALL_DURATION
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
class EthplorerServiceMockImpl : EthplorerService {

    override fun getAddressInfo(address: String) = async {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val eth = EthToken.ETH
            setTokenBalanceInfo(address, eth)
            setTokenPriceInfo(eth)

            val lrc = EthToken.LRC
            setTokenBalanceInfo(address, lrc)
            setTokenPriceInfo(lrc)

            val appc = EthToken.APPC
            setTokenBalanceInfo(address, appc)
            setTokenPriceInfo(appc)

            val req = EthToken.REQ
            setTokenBalanceInfo(address, req)
            setTokenPriceInfo(req)

            listOf(eth, lrc, appc, req)
        } else {
            throw IOException("No connection!")
        }
    }

    override fun getTokenInfo(contractAddress: String) = async {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val tokenList = listOf(EthToken.ETH, EthToken.LRC, EthToken.APPC, EthToken.REQ)
            tokenList.forEach { setTokenPriceInfo(it) }
            tokenList.firstOrNull { it.contractAddress == contractAddress }!!
        } else {
            throw IOException("No connection!")
        }
    }

    private fun setTokenPriceInfo(token: CryptoToken) {
        token.priceInUsd = when (token.identifier) {
            EthToken.ETH.identifier -> BigDecimal("5000.00")
            EthToken.LRC.identifier -> BigDecimal("100.00")
            EthToken.APPC.identifier -> BigDecimal("500.00")
            EthToken.REQ.identifier -> BigDecimal("900.00")
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }
    }

    private fun setTokenBalanceInfo(address: String, token: CryptoToken) {
        val balanceAmount = when (token.identifier) {
            EthToken.ETH.identifier -> BigDecimal("100")
            EthToken.LRC.identifier -> BigDecimal("250")
            EthToken.APPC.identifier -> BigDecimal("0")
            EthToken.REQ.identifier -> BigDecimal("250")
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }

        token.tokenBalances.add(TokenBalanceInfo(address, balanceAmount))
    }

}