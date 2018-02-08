package com.caplaninnovations.looprwallet.models.wallet

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class TradingPair(
        @PrimaryKey var ticker: String? = null
) : RealmObject()