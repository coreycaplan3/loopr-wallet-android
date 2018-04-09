package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOpenOrdersFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_open

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}