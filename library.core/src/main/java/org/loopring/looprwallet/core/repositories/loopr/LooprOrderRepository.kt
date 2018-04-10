package org.loopring.looprwallet.core.repositories.loopr

import io.realm.RealmResults
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprOrderRepository(currentWallet: LooprWallet) : BaseRealmRepository(currentWallet) {

    fun getOrdersByTicker(ticker: String): RealmResults<Any> {
        TODO("Finish this method")
    }

}