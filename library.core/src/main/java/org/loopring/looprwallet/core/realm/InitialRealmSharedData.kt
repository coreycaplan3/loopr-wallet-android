package org.loopring.looprwallet.core.realm

import io.realm.Realm
import org.loopring.looprwallet.core.extensions.upsert
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
        it.upsert(LooprToken.ETH)
        it.upsert(LooprToken.WETH)
        it.upsert(LooprToken.LRC)
        it.upsert(LooprToken.APPC)
        it.upsert(LooprToken.REQ)
        it.upsert(LooprToken.ZRX)

        // Currency Exchange Rate
        it.upsert(CurrencyExchangeRate.USD)
    }

}