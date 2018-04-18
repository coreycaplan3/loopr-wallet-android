package org.loopring.looprwallet.core.networking.eth

import org.loopring.looprwallet.core.utilities.NetworkUtility
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.IOException
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthereumServiceMockImpl : EthereumService {

    private val mockTransactionReceipt = TransactionReceipt(
            "0xabcdef",
            "1",
            "0x123456789",
            "5000000",
            "10000",
            "10000",
            null,
            null,
            null,
            "0x1234567812345678123456781234567812345678",
            "0x1234567812345678123456781234567812345678",
            listOf(),
            null
    )

    override fun sendEther(
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ) = async {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun sendToken(
            contractAddress: String,
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ) = async {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun approveToken(
            contractAddress: String,
            credentials: Credentials,
            spender: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ) = async {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }

    }
}