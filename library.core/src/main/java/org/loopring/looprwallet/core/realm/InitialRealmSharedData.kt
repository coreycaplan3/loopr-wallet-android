package org.loopring.looprwallet.core.realm

import io.realm.Realm
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To setup a newly-created [Realm] with starter data.
 *
 */
object InitialRealmSharedData {

    fun getInitialData() = Realm.Transaction {
        // LooprToken
        it.insertOrUpdate(LooprToken.ETH)
        it.insertOrUpdate(LooprToken.WETH)
        it.insertOrUpdate(LooprToken.LRC)
        it.insertOrUpdate(LooprToken.APPC)
        it.insertOrUpdate(LooprToken.REQ)

        // Currency Exchange Rate
        it.insertOrUpdate(CurrencyExchangeRate.USD)
    }

}