package org.loopring.looprwallet.core.models.blockchain

import android.support.annotation.IntDef

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To describe the state of transactions that are being added to the blockchain.
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