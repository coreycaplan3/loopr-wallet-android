package org.loopring.looprwallet.order.repositories

import android.arch.lifecycle.LiveData
import io.realm.*
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.order.LooprOrderFill
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class OrderFillsRepository(private val currentWallet: LooprWallet) : BaseRealmRepository(true) {

    override fun getAsyncRealm() = realmClient.getPrivateInstance(currentWallet)

    fun getOrderDepth(orderHash: String): LiveData<OrderedRealmCollection<LooprOrderFill>> {
        return uiRealm.where<LooprOrderFill>()
                .equalTo(LooprOrderFill::orderHash, orderHash)
                .sort(LooprOrderFill::tradeDate, Sort.ASCENDING)
                .findAllAsync()
                .asLiveData()
    }

}