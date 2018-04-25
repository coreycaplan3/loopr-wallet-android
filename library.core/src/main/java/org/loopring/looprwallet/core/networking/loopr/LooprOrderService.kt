package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.LooprOrderFill

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LooprOrderService {

    companion object {

        fun getInstance(): LooprOrderService {
            // TODO
            return LooprOrderServiceMockImpl()
        }

    }

    fun getOrdersByAddress(address: String): Deferred<RealmList<LooprOrder>>

    fun getOrderByHash(orderHash: String): Deferred<LooprOrder>

    fun getOrderFillsByOrderHash(orderHash: String): Deferred<RealmList<LooprOrderFill>>

}