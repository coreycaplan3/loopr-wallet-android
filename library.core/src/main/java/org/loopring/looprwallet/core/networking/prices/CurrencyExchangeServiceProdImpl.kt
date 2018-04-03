package org.loopring.looprwallet.core.networking.prices

import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CurrencyExchangeServiceProdImpl: CurrencyExchangeService {

    override fun getCurrentCurrencyExchangeRate(currency: String): Deferred<CurrencyExchangeRate> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}