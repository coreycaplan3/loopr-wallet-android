package org.loopring.looprwallet.core.networking.ethplorer

import io.realm.RealmList
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwalletnetwork.services.EthplorerService

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprEthplorerServiceProdImpl : LooprEthplorerService {

    private val service by lazy {
        EthplorerService.getService()
    }

    override fun getAddressBalances(address: String): Deferred<RealmList<LooprToken>> = async(NET) {
        TODO("...")
    }

    override fun getTokenInfo(contractAddress: String): Deferred<LooprToken> {
        TODO("not implemented")
    }

    override fun getAddressTransferHistory(address: String): Deferred<RealmList<LooprTransfer>> {
        TODO("not implemented")
    }
}