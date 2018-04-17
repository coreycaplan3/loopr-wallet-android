package org.loopring.looprwallet.core.realm

import io.realm.Realm
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
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
        // EthToken
        it.insertOrUpdate(EthToken.ETH)
        it.insertOrUpdate(EthToken.WETH)
        it.insertOrUpdate(EthToken.LRC)
        it.insertOrUpdate(EthToken.APPC)
        it.insertOrUpdate(EthToken.REQ)

        // Currency Exchange Rate
        it.insertOrUpdate(CurrencyExchangeRate.USD)
    }

}