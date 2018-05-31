package org.loopring.looprwallet.core.networking.loopr

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprPriceQuote
import org.loopring.looprwalletnetwork.services.LoopringService

/**
 * Created by corey on 5/31/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprPriceServiceProdImpl: LooprPriceService {

    private val service by lazy {
        LoopringService()
    }

    override fun getPriceQuote(currency: String): Deferred<LooprPriceQuote> {
        return service.getPriceQuote(currency)
    }
}