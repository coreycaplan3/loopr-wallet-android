package org.loopring.looprwallet.core.networking.ethplorer

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.extensions.insertOrUpdate
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.tokens.CryptoToken
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenBalanceInfo
import org.loopring.looprwallet.core.models.loopr.tokens.TokenPriceInfo
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwallet.core.utilities.NetworkUtility.MOCK_SERVICE_CALL_DURATION
import java.io.IOException
import java.math.BigDecimal
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

        val appc = LooprToken.APPC.apply {
            setTokenPriceInfo(this)
        }

        val eth = LooprToken.ETH.apply {
            setTokenPriceInfo(this)
        }

        val lrc = LooprToken.LRC.apply {
            setTokenPriceInfo(this)
        }

        val req = LooprToken.REQ.apply {
            setTokenPriceInfo(this)
        }

        val weth = LooprToken.WETH.apply {
            setTokenPriceInfo(this)
        }

        val zrx = LooprToken.ZRX.apply {
            setTokenPriceInfo(this)
        }

        private fun setTokenPriceInfo(token: LooprToken) {
            val price = when (token.identifier) {
                LooprToken.APPC.identifier -> BigInteger("5000000")
                LooprToken.ETH.identifier, LooprToken.WETH.identifier -> BigInteger("50000000")
                LooprToken.LRC.identifier -> BigInteger("1000000")
                LooprToken.REQ.identifier -> BigInteger("9000000")
                LooprToken.ZRX.identifier -> BigInteger("3000000")
                else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
            }

            val usdPriceInfo = TokenPriceInfo(CurrencySettings.USD).apply {
                this.price = price
            }
            token.tokenPrices.insertOrUpdate(usdPriceInfo) {
                it.currency == CurrencySettings.USD
            }

            val cnyPriceInfo = TokenPriceInfo(CurrencySettings.CNY).apply {
                // 1 USD == ~6.41 CNY
                this.price = (price.toBigDecimal() * BigDecimal(6.41)).toBigInteger()
            }
            token.tokenPrices.insertOrUpdate(cnyPriceInfo) {
                it.currency == CurrencySettings.CNY
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
            transactionFeeUsdValue = BigInteger("24")
    ).apply { this.token = lrc }

    private val transfer2 = LooprTransfer(
            transactionHash = "abcdef4321",
            blockNumber = BigInteger("49999"),
            isSend = false,
            contactAddress = "0x0123456789abcdef0123456789abcdef01234567",
            numberOfTokens = BigInteger("3000000000000000000"),
            usdValue = BigInteger("63298"),
            transactionFee = BigInteger("1"),
            transactionFeeUsdValue = BigInteger("20")
    ).apply { this.token = appc }

    private val transfer3 = LooprTransfer(
            transactionHash = "abcdef4213",
            blockNumber = BigInteger("4999988"),
            isSend = true,
            contactAddress = "0x0123456789abcdef0123456789abcdef01234567",
            numberOfTokens = BigInteger("525000000000000000"),
            usdValue = BigInteger("48726"),
            transactionFee = BigInteger("1"),
            transactionFeeUsdValue = BigInteger("25")
    ).apply { this.token = req }

    override fun getAddressBalances(address: String): Deferred<RealmList<LooprToken>> = async(NET) {
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            setTokenBalanceInfo(address, appc)
            setTokenBalanceInfo(address, eth)
            setTokenBalanceInfo(address, lrc)
            setTokenBalanceInfo(address, req)
            setTokenBalanceInfo(address, weth)
            setTokenBalanceInfo(address, zrx)

            return@async RealmList<LooprToken>(appc, eth, lrc, req, weth, zrx)
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
        val balance = when (token.identifier) {
            LooprToken.APPC.identifier -> BigInteger("78132890000000000000000")
            LooprToken.ETH.identifier -> BigInteger("8230000000000000000")
            LooprToken.LRC.identifier -> BigInteger("50000000000000000000000")
            LooprToken.REQ.identifier -> BigInteger("25000000000000000000000")
            LooprToken.WETH.identifier -> BigInteger("5250000000000000000")
            LooprToken.ZRX.identifier -> BigInteger.ZERO // Never own ZRX :-)
            else -> throw IllegalArgumentException("Invalid token, found: ${token.identifier}")
        }

        val balanceInfo = TokenBalanceInfo().apply {
            this.address = address
            this.balance = balance
        }
        token.tokenBalances.insertOrUpdate(balanceInfo) {
            it.address == address
        }
    }

}