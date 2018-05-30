package org.loopring.looprwallet.createorder.fragments

import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.createorder.R

/**
 * Created by corey on 5/30/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class OrderPlacedFragment : BaseFragment() {

    companion object {

        val TAG: String = OrderPlacedFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_order_placed

}