package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET

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
            val buildFlavor = BuildUtility.BUILD_FLAVOR
            return when (buildFlavor) {
                FLAVOR_MOCKNET -> LooprOrderServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> LooprOrderServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildFlavor")
            }
        }

    }

    fun getOrdersByAddress(orderFilter: OrderFilter): Deferred<LooprOrderContainer>

    fun getOrderByHash(orderHash: String): Deferred<LooprOrder>

    fun getOrderFillsByOrderHash(orderHash: String): Deferred<RealmList<LooprOrderFill>>

}