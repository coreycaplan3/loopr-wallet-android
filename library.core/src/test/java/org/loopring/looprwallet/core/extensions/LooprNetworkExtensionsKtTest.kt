package org.loopring.looprwallet.core.extensions

import org.junit.Test

import org.junit.Assert.*
import org.loopring.looprwalletnetwork.models.loopring.LooprTicker

class LooprNetworkExtensionsKtTest {

    @Test
    fun getTickers() {
        val primaryTicker = "LRC"
        val secondaryTicker = "WETH"
        val market = "$primaryTicker-$secondaryTicker"

        val ticker = LooprTicker().apply {
            this.market = market
        }

        assertEquals(primaryTicker, ticker.primaryTicker)
        assertEquals(secondaryTicker, ticker.secondaryTicker)
    }

}