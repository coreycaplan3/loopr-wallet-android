package com.caplaninnovations.looprwallet.realm

import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import io.realm.Realm

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
        it.insertOrUpdate(EthToken.LRC)
        it.insertOrUpdate(EthToken.APPC)
        it.insertOrUpdate(EthToken.REQ)

        // Currency Exchange Rate
        it.insertOrUpdate(CurrencyExchangeRate.USD)
    }

}