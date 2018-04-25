package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.order.LooprOrder
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

    fun getOrderByHash(orderHash: String): LiveData<LooprOrder> {
        return uiRealm.where<LooprOrder>()
                .equalTo(LooprOrder::orderHash, orderHash)
                .findFirstAsync()
                .asLiveData()
    }

    fun getOrders(orderFilter: OrderFilter): LiveData<OrderedRealmCollection<LooprOrder>> {
        // TODO apply order filter
        return uiRealm.where<LooprOrder>()
                .sort(LooprOrder::orderDate)
                .findAllAsync()
                .asLiveData()
    }

}