package org.loopring.looprwallet.core.repositories.loopr

import io.realm.RealmResults
import org.loopring.looprwallet.core.models.order.OrderFilter
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
class LooprOrderRepository(private val currentWallet: LooprWallet) : BaseRealmRepository() {

    override fun getRealm() = realmClient.getPrivateInstance(currentWallet)

    fun getOrdersByTicker(orderFilter: OrderFilter, ticker: String? = null): RealmResults<Any> {
        TODO("IMPLEMENT ME")
    }

}