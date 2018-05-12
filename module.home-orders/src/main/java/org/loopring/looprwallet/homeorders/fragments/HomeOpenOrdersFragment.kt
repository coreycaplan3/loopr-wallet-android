package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_OPEN_ALL
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.HomeOrderAdapter
import org.loopring.looprwallet.orderdetails.viewmodels.CancelAllOrdersViewModel

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeOpenOrdersFragment : BaseHomeChildOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)

    override val recyclerView: RecyclerView?
        get() = findViewById(R.id.fragmentContainer)

    @VisibleForTesting
    val cancelAllOrdersViewModel: CancelAllOrdersViewModel by lazy {
        LooprViewModelFactory.get<CancelAllOrdersViewModel>(this)
    }

    override fun provideAdapter(savedInstanceState: Bundle?, address: String): HomeOrderAdapter {
        val activity = activity as? BaseActivity
                ?: throw IllegalStateException("Activity cannot be cast!")

        return HomeOrderAdapter(savedInstanceState, address, FILTER_OPEN_ALL, activity, this, ::onCancelAllClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelAllOrdersViewModel.result.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            view.context.longToast(R.string.your_orderes_will_be_cancelled)
        }
        setupTransactionViewModel(cancelAllOrdersViewModel, R.string.cancelling_all_open_orders, false, null)
    }

    // MARK - Private Methods

    private fun onCancelAllClick() {
        val context = context ?: return
        AlertDialog.Builder(context)
                .setTitle(R.string.cancel_all_orders)
                .setMessage(R.string.cancel_all_rationale)
                .setPositiveButton(R.string.cancel_all) { dialog, _ ->
                    dialog.dismiss()

                    val wallet = walletClient.getCurrentWallet()
                    if (wallet != null) {
                        cancelAllOrdersViewModel.cancelAllOrders(wallet)
                    }
                }
                .setNegativeButton(R.string.exit) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

}