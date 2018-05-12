package org.loopring.looprwallet.core.realm

import io.realm.Realm
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To setup a newly-created [Realm] with starter data.
 *
 */
object InitialRealmData {

    fun getInitialData() = Realm.Transaction {
        getTransactionBody(it)
    }

    fun getTransactionBody(realm: Realm) {
        realm.apply {

            // LooprToken
            upsert(LooprToken.ETH)
            upsert(LooprToken.WETH)
            upsert(LooprToken.LRC)
            upsert(LooprToken.APPC)
            upsert(LooprToken.REQ)
            upsert(LooprToken.ZRX)

            // Currency Exchange Rate
            upsert(CurrencyExchangeRate.USD)

        }
    }

}