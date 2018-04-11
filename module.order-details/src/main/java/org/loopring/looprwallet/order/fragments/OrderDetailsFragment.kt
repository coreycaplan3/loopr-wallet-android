package org.loopring.looprwallet.order.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_order_details.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.order.R
import org.loopring.looprwallet.order.adapters.OrderDetailsAdapter

/**
 * Created by Corey on 4/9/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Displays the details for an order, after clicking on it.
 */
class OrderDetailsFragment : BaseFragment() {

    companion object {

        val TAG: String = OrderDetailsFragment::class.java.simpleName!!

        private const val KEY_ORDER = "_ORDER"

        fun getInstance(orderId: String) = OrderDetailsFragment().apply {
            arguments = bundleOf(KEY_ORDER to orderId)
        }
    }

    override var navigationIcon: Int = R.drawable.ic_close_white_24dp

    override val layoutResource: Int
        get() = R.layout.fragment_order_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setTitle(R.string.order_details)

        // TODO check if order is complete; remove listener & disable after refresh if it's complete
        val isComplete = true
        if (isComplete) {
            orderDetailsSwipeRefreshLayout.isEnabled = false
        } else {
            orderDetailsSwipeRefreshLayout.setOnRefreshListener {
                TODO("REFRESH VIEW MODEL, IF NECESSARY")
            }
        }

        orderDetailsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        orderDetailsRecyclerView.adapter = OrderDetailsAdapter(TODO("FINISH ME"))
    }

}