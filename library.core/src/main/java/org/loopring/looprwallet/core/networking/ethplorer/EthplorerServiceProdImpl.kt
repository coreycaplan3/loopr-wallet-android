package org.loopring.looprwallet.core.networking.ethplorer

import io.realm.RealmList
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthplorerServiceProdImpl : EthplorerService {

    override fun getAddressInfo(address: String): Deferred<RealmList<LooprToken>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTokenInfo(contractAddress: String): Deferred<LooprToken> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}