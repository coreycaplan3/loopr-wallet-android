package org.loopring.looprwallet.order.repositories

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class OrderFillsRepository(private val currentWallet: LooprWallet) : BaseRealmRepository() {

    override fun getRealm() = realmClient.getPrivateInstance(currentWallet)

    fun getOrderDepth(orderId: String): LiveData<OrderedRealmCollection<RealmModel>> {
        TODO("Finish me...")
    }

}