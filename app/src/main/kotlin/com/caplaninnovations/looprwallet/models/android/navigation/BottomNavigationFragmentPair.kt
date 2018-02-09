package com.caplaninnovations.looprwallet.models.android.navigation

import android.support.annotation.DrawableRes
import android.support.annotation.StringDef
import android.support.annotation.StringRes
import com.caplaninnovations.looprwallet.fragments.BaseFragment

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
                                   @DrawableRes val drawableResource: Int,
                                   @StringRes val textResource: Int) {

    companion object {

        const val KEY_MARKETS = "_MARKETS"
        const val KEY_ORDERS = "_ORDERS"
        const val KEY_MY_WALLET = "_MY_WALLET"
    }

}