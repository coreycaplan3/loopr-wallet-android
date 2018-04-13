package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_orders.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_FILLED
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeFilledOrdersFragment : BaseHomeOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val recyclerView: RecyclerView
        get() = fragmentContainer

    override fun provideAdapter(savedInstanceState: Bundle?): GeneralOrderAdapter {
        val activity = activity as? BaseActivity
                ?: throw IllegalStateException("Cannot cast activity")
        return GeneralOrderAdapter(savedInstanceState, FILTER_FILLED, activity, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetOrderLiveData()
    }

}