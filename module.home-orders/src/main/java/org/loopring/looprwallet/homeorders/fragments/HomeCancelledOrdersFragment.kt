package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_orders.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey on 4/11/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment in a set of tabs for displaying the user's cancelled orders
 */
class HomeCancelledOrdersFragment: BaseHomeOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val recyclerView: RecyclerView
        get() = fragmentContainer

    override fun provideAdapter(savedInstanceState: Bundle?): GeneralOrderAdapter {
        val activity = activity as? BaseActivity ?: throw IllegalStateException("Activity cannot be cast!")
        return GeneralOrderAdapter(savedInstanceState, OrderFilter.FILTER_CANCELLED, activity, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetOrderLiveData()
    }

    // MARK - Private Methods

    private fun onCancelAllClick() {
        TODO("IMPLEMENT ME")
    }

}