package org.loopring.looprwallet.core.networking.ethplorer

import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.models.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
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
            val eth = LooprToken.ETH
            setTokenBalanceInfo(address, eth)
            setTokenPriceInfo(eth)

            val lrc = LooprToken.LRC
            setTokenBalanceInfo(address, lrc)
            setTokenPriceInfo(lrc)

            val appc = LooprToken.APPC
            setTokenBalanceInfo(address, appc)
            setTokenPriceInfo(appc)

            val req = LooprToken.REQ
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
            val tokenList = listOf(LooprToken.ETH, LooprToken.LRC, LooprToken.APPC, LooprToken.REQ)
            tokenList.forEach { setTokenPriceInfo(it) }
            tokenList.firstOrNull { it.contractAddress == contractAddress }!!
        } else {
            throw IOException("No connection!")
        }
    }

    private fun setTokenPriceInfo(token: CryptoToken) {
        token.priceInUsd = when (token.identifier) {
            LooprToken.ETH.identifier -> BigDecimal("5000.00")
            LooprToken.LRC.identifier -> BigDecimal("100.00")
            LooprToken.APPC.identifier -> BigDecimal("500.00")
            LooprToken.REQ.identifier -> BigDecimal("900.00")
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }
    }

    private fun setTokenBalanceInfo(address: String, token: CryptoToken) {
        val balanceAmount = when (token.identifier) {
            LooprToken.ETH.identifier -> BigDecimal("100")
            LooprToken.LRC.identifier -> BigDecimal("250")
            LooprToken.APPC.identifier -> BigDecimal("0")
            LooprToken.REQ.identifier -> BigDecimal("250")
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }

        token.tokenBalances.add(TokenBalanceInfo(address, balanceAmount))
    }

}