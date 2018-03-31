package com.caplaninnovations.looprwallet.models.user

import android.support.annotation.StringDef
import io.realm.RealmObject
import io.realm.annotations.Index
import java.util.*

/**
 * Created by Corey on 3/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class SyncData(
        @Index var syncType: String = "",
        @Index var address: String? = null,
        var lastSyncTime: Date? = null
) : RealmObject() {

    @StringDef(SYNC_TYPE_TOKEN_BALANCE)
    annotation class SyncType

    companion object {
        const val SYNC_TYPE_TOKEN_BALANCE = "_TOKEN_BALANCE"
        const val SYNC_TYPE_TOKEN_PRICE = "_TOKEN_PRICE"
        const val SYNC_TYPE_CURRENCY_EXCHANGE_RATE = "_CURRENCY_EXCHANGE_RATE"
    }

}