package org.loopring.looprwallet.orderdetails.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_order_details.*
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.orders.*
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.adapters.OrderDetailsAdapter
import org.loopring.looprwallet.core.models.loopr.orders.OrderFillsLooprPager
import org.loopring.looprwallet.core.utilities.RealmUtility
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
import org.loopring.looprwallet.orderdetails.viewmodels.CancelOrderViewModel
import org.loopring.looprwallet.orderdetails.viewmodels.OrderFillsViewModel
import org.loopring.looprwallet.orderdetails.viewmodels.OrderSummaryViewModel
import javax.inject.Inject

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

        private const val KEY_ORDER_FILTER = "ORDER_FILTER"

        fun getInstance(orderHash: String) = OrderDetailsFragment().apply {
            arguments = bundleOf(KEY_ORDER to orderHash)
        }
    }

    private val orderHash: String by lazy {
        arguments!!.getString(KEY_ORDER)
    }

    override var navigationIcon: Int = R.drawable.ic_close_white_24dp

    private var adapter: OrderDetailsAdapter? = null

    override val layoutResource: Int
        get() = R.layout.fragment_order_details

    @VisibleForTesting
    val cancelOrderViewModel: CancelOrderViewModel by lazy {
        LooprViewModelFactory.get<CancelOrderViewModel>(this)
    }

    @VisibleForTesting
    val orderSummaryViewModel: OrderSummaryViewModel by lazy {
        LooprViewModelFactory.get<OrderSummaryViewModel>(this)
    }

    @VisibleForTesting
    val orderFillsViewModel: OrderFillsViewModel by lazy {
        LooprViewModelFactory.get<OrderFillsViewModel>(this)
    }

    @Inject
    lateinit var currencySettings: CurrencySettings

    private lateinit var orderFilter: OrderFillFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderFilter = savedInstanceState?.getParcelable(KEY_ORDER_FILTER) ?: OrderFillFilter(orderHash, 1)

        orderDetailsLooprComponent.inject(this)

        toolbarDelegate?.onCreateOptionsMenu = ::createOptionsMenu
        toolbarDelegate?.onOptionsItemSelected = ::optionsItemSelected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setTitle(R.string.order_details)
        toolbar?.navigationIcon = drawable(R.drawable.ic_clear_white_24dp, view.context)

        cancelOrderViewModel.result.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            view.context.longToast(R.string.your_orders_will_be_cancelled)
        }
        setupTransactionViewModel(cancelOrderViewModel, R.string.cancelling_all_open_orders, false, null)

        orderSummaryViewModel.getOrderByHash(this, orderHash) {
            when {
                adapter != null -> adapter?.orderSummary = it
                else -> setupAdapter(view, it)
            }
            bindLooprOrder(it)
        }
        setupOfflineFirstStateAndErrorObserver(orderSummaryViewModel, orderDetailsSwipeRefreshLayout)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_ORDER_FILTER, orderFilter)
    }

    // MARK - Private Methods

    private fun onLoadMore() {
        adapter?.pager?.let { pager ->
            RealmUtility.loadMorePaging(pager, orderFilter, orderFillsViewModel)
        }
    }

    private fun createOptionsMenu(toolbar: Toolbar?) {
        toolbar?.menu?.clear()

        orderSummaryViewModel.data?.ifNotNull {
            if (it.isOpen) {
                toolbar?.inflateMenu(R.menu.menu_order_details)
            }
        }
    }

    private fun optionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.orderDetailsCancelOrder -> {
                val context = context ?: return false
                val order = orderSummaryViewModel.data ?: return false

                AlertDialog.Builder(context)
                        .setTitle(R.string.question_cancel_order)
                        .setMessage(R.string.rationale_cancel_order)
                        .setPositiveButton(R.string.cancel_order) { dialog, _ ->
                            dialog.dismiss()

                            val wallet = walletClient.getCurrentWallet()
                            if (wallet != null) {
                                cancelOrderViewModel.cancelOrder(wallet, order)
                            }
                        }.setNegativeButton(R.string.exit) { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()

                return true
            }
        }

        return false
    }

    private fun setupAdapter(view: View, looprOrder: AppLooprOrder) {
        val adapter = OrderDetailsAdapter(looprOrder).apply {
            adapter = this
            onLoadMore = ::onLoadMore
        }

        orderDetailsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        orderDetailsRecyclerView.adapter = adapter

        // We can now retrieve order fills, since we initialized the adapter with the order data
        orderFillsViewModel.getOrderFills(this, orderFilter) {
            (adapter.pager as? OrderFillsLooprPager)?.orderFillContainer = it
            setupOfflineFirstDataObserverForAdapter(orderFillsViewModel, adapter, it.data)
        }
        setupOfflineFirstStateAndErrorObserver(orderFillsViewModel, orderDetailsSwipeRefreshLayout)
    }

    @SuppressLint("SetTextI18n")
    private fun bindLooprOrder(order: AppLooprOrder) {

        // Title
        val primaryTicker = order.tradingPair.primaryTicker
        val secondaryTicker = order.tradingPair.secondaryTicker
        orderDetailsTitleLabel.text = "$primaryTicker / $secondaryTicker"

        // Buy/Sell Image
        if (order.isSell) {
            orderDetailsSellCircle.visibility = View.VISIBLE
            orderDetailsBuyCircle.visibility = View.GONE
        } else {
            orderDetailsBuyCircle.visibility = View.VISIBLE
            orderDetailsSellCircle.visibility = View.GONE
        }

        // Order Progress
        // TODO standardize this binding between order details and the home orders fragment
        when (order.status) {
            OrderSummaryFilter.FILTER_OPEN_NEW -> {
                orderDetailsOrderProgressView.visibility = View.VISIBLE
                orderDetailsOrderProgressView.progress = 0

                orderDetailsOrderCompleteImage.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_OPEN_PARTIAL -> {
                orderDetailsOrderProgressView.visibility = View.VISIBLE
                orderDetailsOrderProgressView.progress = order.percentageFilled

                orderDetailsOrderCompleteImage.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_FILLED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_swap_horiz_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_CANCELLED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_cancel_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_EXPIRED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_alarm_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
        }

        // Token Amount
        orderDetailsTokenAmountLabel.text = order.amount.formatAsToken(currencySettings, order.tradingPair.primaryToken)

        // Price
        order.tradingPair.secondaryToken.let { token ->
            val priceInSecondary = order.priceInSecondaryTicker.formatAsToken(currencySettings, token)
            val primaryTokenTicker = order.tradingPair.primaryToken.ticker
            orderDetailsFiatAmountLabel.text = "@ $priceInSecondary / $primaryTokenTicker"
        }

        orderDetailsSwipeRefreshLayout.isEnabled = !order.isComplete
    }

}