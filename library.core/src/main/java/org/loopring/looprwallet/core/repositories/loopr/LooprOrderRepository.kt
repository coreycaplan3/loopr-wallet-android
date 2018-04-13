package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.RealmModel
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

    fun getOrders(orderFilter: OrderFilter): LiveData<RealmResults<RealmModel>> {
        TODO("IMPLEMENT ME")
    }

}