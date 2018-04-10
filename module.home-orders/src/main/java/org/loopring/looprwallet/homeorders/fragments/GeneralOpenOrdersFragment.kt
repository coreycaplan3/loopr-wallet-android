package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_orders.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.core.viewmodels.LooprWalletViewModelFactory
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter
import org.loopring.looprwallet.homeorders.viewmodels.GeneralOrderViewModel

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOpenOrdersFragment : BaseGeneralOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val recyclerView: RecyclerView
        get() = fragmentContainer

    private var generalOrderViewModel: GeneralOrderViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null

            field = LooprWalletViewModelFactory.get(this, wallet)
            return field
        }

    override fun provideAdapter() = GeneralOrderAdapter(true, activity as BaseActivity)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderFilter = OrderFilter(adapter.currentDateFilter, adapter.currentStatusFilter)
        generalOrderViewModel?.getOpenOrders(orderFilter)
        TODO("not implemented")
    }

    override fun onQueryTextChangeListener(searchQuery: String) {
        TODO("not implemented") // TODO
    }

}