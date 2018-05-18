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
            copyToRealm(LooprToken.ETH)
            copyToRealm(LooprToken.WETH)
            copyToRealm(LooprToken.LRC)
            copyToRealm(LooprToken.APPC)
            copyToRealm(LooprToken.REQ)
            copyToRealm(LooprToken.ZRX)

            // Currency Exchange Rate
            copyToRealm(CurrencyExchangeRate.USD)

        }
    }

}