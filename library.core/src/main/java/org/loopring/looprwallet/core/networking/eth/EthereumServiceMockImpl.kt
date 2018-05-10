package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
internal class EthereumServiceMockImpl : EthereumService() {

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
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun sendToken(
            contractAddress: String,
            binary: String,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun approveToken(
            contractAddress: String,
            binary: String,
            credentials: Credentials,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> mockTransactionReceipt
            else -> throw IOException("No connection!")
        }

    }
}