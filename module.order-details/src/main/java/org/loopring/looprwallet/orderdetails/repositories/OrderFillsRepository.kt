package org.loopring.looprwallet.orderdetails.repositories

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class OrderFillsRepository : BaseRealmRepository() {

    fun getOrderDepth(orderHash: String, context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprOrderFill>> {
        return getRealmFromContext(context).where<LooprOrderFill>()
                .equalTo(LooprOrderFill::orderHash, orderHash)
                .sort(LooprOrderFill::tradeDate, Sort.ASCENDING)
                .findAllAsync()
                .asLiveData()
    }

}