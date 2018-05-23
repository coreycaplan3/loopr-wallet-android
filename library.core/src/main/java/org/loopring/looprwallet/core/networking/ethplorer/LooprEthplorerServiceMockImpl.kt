package org.loopring.looprwallet.core.networking.ethplorer

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.tokens.CryptoToken
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenBalanceInfo
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwallet.core.utilities.NetworkUtility.MOCK_SERVICE_CALL_DURATION
import java.io.IOException
import java.math.BigInteger

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprEthplorerServiceMockImpl : LooprEthplorerService {

    companion object {

        val eth = LooprToken.ETH.apply {
            setTokenPriceInfo(this)
        }

        val lrc = LooprToken.LRC.apply {
            setTokenPriceInfo(this)
        }

        val appc = LooprToken.APPC.apply {
            setTokenPriceInfo(this)
        }

        val req = LooprToken.REQ.apply {
            setTokenPriceInfo(this)
        }

        val zrx = LooprToken.ZRX.apply {
            setTokenPriceInfo(this)
        }

        private fun setTokenPriceInfo(token: CryptoToken) {
            token.priceInUsd = when (token.identifier) {
                LooprToken.ETH.identifier -> BigInteger("500000")
                LooprToken.LRC.identifier -> BigInteger("10000")
                LooprToken.APPC.identifier -> BigInteger("50000")
                LooprToken.REQ.identifier -> BigInteger("90000")
                LooprToken.ZRX.identifier -> BigInteger("30000")
                else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
            }
        }

    }

    // The current block number when mocking is 5m

    private val transfer1 = LooprTransfer(
            transactionHash = "abcdef1234",
            blockNumber = BigInteger("4999998"),
            isSend = false,
            contactAddress = "0x0123456789abcdef0123456789abcdef01234567",
            numberOfTokens = BigInteger("20000000000000000000"),
            usdValue = BigInteger("85493"),
            transactionFee = BigInteger("1"),
            transactionFeeUsdValue = BigInteger("24"),
            token = lrc
    )

    private val transfer2 = LooprTransfer(
            transactionHash = "abcdef4321",
            blockNumber = BigInteger("49999"),
            isSend = false,
            contactAddress = "0x0123456789abcdef0123456789abcdef01234567",
            numberOfTokens = BigInteger("3000000000000000000"),
            usdValue = BigInteger("63298"),
            transactionFee = BigInteger("1"),
            transactionFeeUsdValue = BigInteger("20"),
            token = appc
    )

    private val transfer3 = LooprTransfer(
            transactionHash = "abcdef4213",
            blockNumber = BigInteger("4999988"),
            isSend = true,
            contactAddress = "0x0123456789abcdef0123456789abcdef01234567",
            numberOfTokens = BigInteger("525000000000000000"),
            usdValue = BigInteger("48726"),
            transactionFee = BigInteger("1"),
            transactionFeeUsdValue = BigInteger("25"),
            token = req
    )

    override fun getAddressBalances(address: String): Deferred<RealmList<LooprToken>> = async(NET) {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            setTokenBalanceInfo(address, eth)
            setTokenBalanceInfo(address, appc)
            setTokenBalanceInfo(address, lrc)
            setTokenBalanceInfo(address, req)
            setTokenBalanceInfo(address, zrx)

            return@async RealmList<LooprToken>(eth, lrc, appc, req)
        } else {
            throw IOException("No connection!")
        }
    }

    override fun getTokenInfo(contractAddress: String): Deferred<LooprToken> = async(NET) {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val tokenList = listOf(LooprToken.ETH, LooprToken.LRC, LooprToken.APPC, LooprToken.REQ)
            tokenList.forEach { setTokenPriceInfo(it) }
            tokenList.firstOrNull { it.identifier == contractAddress }!!
        } else {
            throw IOException("No connection!")
        }
    }

    override fun getAddressTransferHistory(address: String): Deferred<RealmList<LooprTransfer>> = async(NET) {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(transfer1, transfer2, transfer3)
        } else {
            throw IOException("No connection!")
        }
    }

    // MARK - Private Methods

    private fun setTokenBalanceInfo(address: String, token: CryptoToken) {
        val balanceAmount = when (token.identifier) {
            LooprToken.ETH.identifier -> BigInteger("8230000000000000000")
            LooprToken.LRC.identifier -> BigInteger("50000000000000000000000")
            LooprToken.APPC.identifier -> BigInteger("78132890000000000000000")
            LooprToken.REQ.identifier -> BigInteger("25000000000000000000000")
            LooprToken.ZRX.identifier -> BigInteger("100000000000000000000")
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }

        token.tokenBalances.add(TokenBalanceInfo(address, balanceAmount))
    }

}