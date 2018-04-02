package com.caplaninnovations.looprwallet.networking.eth

import org.loopring.looprwallet.core.utilities.NetworkUtility
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
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

    override fun sendEther(recipient: String, amount: BigDecimal, gasLimit: BigDecimal, gasPrice: BigDecimal) = async {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            TransactionReceipt()
        } else {
            throw IOException("No Connection!")
        }
    }

    override fun sendToken(contractAddress: String, recipient: String, amount: BigDecimal, gasLimit: BigDecimal, gasPrice: BigDecimal) = async {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            TransactionReceipt()
        } else {
            throw IOException("No Connection!")
        }
    }

}