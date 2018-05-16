package org.loopring.looprwallet.core.networking.loopr

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.orders.*
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

    fun submitOrder(looprOrder: AppLooprOrder): Deferred<Unit>

    fun getOrdersByAddress(orderFilter: OrderSummaryFilter): Deferred<LooprOrderContainer>

    fun getOrderByHash(orderHash: String): Deferred<AppLooprOrder>

    fun getOrderFillsByOrderHash(orderFillFilter: OrderFillFilter): Deferred<LooprOrderFillContainer>

}