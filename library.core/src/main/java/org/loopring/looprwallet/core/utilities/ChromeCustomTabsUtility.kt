package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.support.customtabs.CustomTabsIntent
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col


/**
 *  Created by Corey on 4/23/2018.
 *
 *  Project: loopr-android
 *
 *  Purpose of Class:
 *
 */
object ChromeCustomTabsUtility {

    const val ETHERSCAN_ADDRESS_URI = "https://etherscan.io/address/"
    const val ETHERSCAN_TX_URI = "https://etherscan.io/tx/"

    fun getInstance(context: Context): CustomTabsIntent {
        return CustomTabsIntent.Builder()
                .setToolbarColor(col(context.theme.getResourceIdFromAttrId(R.attr.colorPrimary)))
                .setShowTitle(true)
                .addDefaultShareMenuItem()
                .build()
    }

}