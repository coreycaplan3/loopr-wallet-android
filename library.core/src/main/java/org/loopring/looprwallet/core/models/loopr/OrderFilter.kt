package org.loopring.looprwallet.core.models.loopr

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
data class OrderFilter(val dateFilter: String, val statusFilter: String) {

    companion object {
        val FILTER_ALL: String = str(R.string.filter_all)
    }

}