package org.loopring.looprwallet.viewbalances.adapters

import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class: A listener that sends token approve/disapprove events to the implementor. For
 * more information, see the ERC-20 standard's *approve* function.
 */
interface OnTokenLockClickListener {

    /**
     * Called when the user clicks on the lock button for a [LooprToken].
     *
     * @param token The [LooprToken] that was clicked
     */
    fun onTokenLockClick(token: LooprToken)
}