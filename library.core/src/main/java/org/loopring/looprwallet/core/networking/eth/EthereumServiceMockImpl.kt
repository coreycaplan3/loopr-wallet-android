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

    companion object {

        val mockTransactionReceipt = TransactionReceipt(
                "0xad0860212c3985a8cd46e82118ceb097e1b7e7921ebe3b4f1272b2453805c049",
                "1",
                "0x88bcbc5679662927729e5d64846647af4b3b8047cb2aaf5bc2bd8c30d2ed2339",
                "5000000",
                "21000",
                "21000",
                null,
                null,
                null,
                "0x1234567812345678123456781234567812345678",
                "0x1234567812345678123456781234567812345678",
                listOf(),
                null
        )

    }

    override fun sendEther(
            recipient: String,
            amount: BigInteger,
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

    override fun depositWeth(
            contractAddress: String,
            credentials: Credentials,
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

    override fun withdrawWeth(
            contractAddress: String,
            credentials: Credentials,
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