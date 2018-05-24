package org.loopring.looprwallet.createorder.fragments

import androidx.os.bundleOf
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.createorder.R

/**
 * Created by Corey on 2/6/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateOrderFragment : BaseFragment() {

    companion object {

        val TAG: String = CreateOrderFragment::class.java.simpleName

        private const val KEY_TRADING_PAIR = "TRADING_PAIR"

        fun getInstance(market: String?) = CreateOrderFragment().apply {
            arguments = bundleOf(KEY_TRADING_PAIR to market)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_order

    private val market: String? by lazy {
        arguments?.getString(KEY_TRADING_PAIR)
    }

}