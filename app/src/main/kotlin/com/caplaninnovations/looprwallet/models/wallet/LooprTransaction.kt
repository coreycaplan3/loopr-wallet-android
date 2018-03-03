package com.caplaninnovations.looprwallet.models.wallet

import android.support.annotation.IntDef

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprTransaction {

    companion object {
        const val WAITING = 1
        const val PENDING = 2
        const val COMPLETE = 3
    }

    @IntDef(value = [WAITING, PENDING, COMPLETE])
    annotation class TransactionStatus

}