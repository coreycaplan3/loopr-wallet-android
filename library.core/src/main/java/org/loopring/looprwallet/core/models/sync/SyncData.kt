package org.loopring.looprwallet.core.models.sync

import android.support.annotation.StringDef
import io.realm.RealmObject
import io.realm.annotations.Index
import java.util.*

/**
 * Created by Corey on 3/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To record when the user syncs data for a specific realm. This is used to make
 * intelligent decisions whether it's necessary to refresh. Ultimately, this class is indirectly
 * responsible for saving battery and data.
 */
open class SyncData(
        @Index var syncType: String = "",
        @Index var address: String? = null,
        var lastSyncTime: Date? = null
) : RealmObject() {

    @StringDef(SYNC_TYPE_NONE, SYNC_TYPE_TOKEN_BALANCE, SYNC_TYPE_CURRENCY_EXCHANGE_RATE)
    annotation class SyncType

    companion object {
        const val SYNC_TYPE_NONE = "_NONE"

        const val SYNC_TYPE_TOKEN_BALANCE = "_TOKEN_BALANCE"
        const val SYNC_TYPE_TOKEN_PRICE = "_TOKEN_PRICE"
        const val SYNC_TYPE_CURRENCY_EXCHANGE_RATE = "_CURRENCY_EXCHANGE_RATE"
    }

}