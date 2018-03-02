package com.caplaninnovations.looprwallet.models.wallet.transfer

import com.caplaninnovations.looprwallet.models.wallet.LooprTransaction

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To standardize how transfers are done (so this class can be used on different
 * blockchains, potentially).
 *
 */
interface LooprTransfer {

    /**
     * The hash of the transaction
     */
    val transactionHash: String

    /**
     * The time at which the transfer occurred. This is measured as UNIX timestamp (in millis)
     */
    val timestamp: Long

    /**
     * The block number at which the transfer completed
     */
    val blockNumber: Long

    /**
     * The number of tokens transferred
     */
    val numberOfTokens: Double

    /**
     * The value of the currency at the time of the transaction
     */
    val currencyValue: Double

    /**
     * The amount paid by the sender to execute this transfer
     */
    val transactionFee: Double

    /**
     * The current status of the transaction
     */
    @LooprTransaction.TransactionStatus
    val status: Int

}