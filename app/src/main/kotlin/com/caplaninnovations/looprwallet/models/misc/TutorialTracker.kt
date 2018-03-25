package com.caplaninnovations.looprwallet.models.misc

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To track whether or not a given wallet has seen a given tutorial or not
 *
 * @param walletName The name of the wallet, which is used as the (unique) primary key
 * @param isViewTransfersDismissed True if the ViewTransferViewHolder tutorial has been dismissed
 * by the user or false otherwise.
 */
open class TutorialTracker(
        @PrimaryKey var walletName: String = "",
        var isViewTransfersDismissed: Boolean = false
) : RealmObject()
