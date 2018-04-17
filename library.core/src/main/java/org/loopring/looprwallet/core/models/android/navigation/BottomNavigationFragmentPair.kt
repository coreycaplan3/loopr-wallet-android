package org.loopring.looprwallet.core.models.android.navigation

import android.support.annotation.IdRes
import org.loopring.looprwallet.core.fragments.BaseFragment

/**
 * Created by Corey Caplan on 2/8/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class BottomNavigationFragmentPair(@BottomNavigationTag val tag: String,
                                   val fragment: BaseFragment,
                                   @IdRes val menuId: Int) {

    companion object {

        const val KEY_MARKETS = "_MARKETS"
        const val KEY_ORDERS = "_ORDERS"
        const val KEY_TRANSFERS = "_TRANSFERS"
        const val KEY_MY_WALLET = "_MY_WALLET"
    }

}