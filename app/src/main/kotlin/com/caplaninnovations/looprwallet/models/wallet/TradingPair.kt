package com.caplaninnovations.looprwallet.models.wallet

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class TradingPair : RealmObject {

    @PrimaryKey
    val ticker: String

    constructor(ticker: String) {
        this.ticker = ticker
    }

}