package org.loopring.looprwallet.orderdetails.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_order_details.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.adapters.OrderDetailsAdapter
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
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

    private var orderSummaryViewModel: OrderSummaryViewModel? = null
        @Synchronized
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            field = LooprViewModelFactory.get(this, wallet)
            return field
        }

    private var orderFillsViewModel: OrderFillsViewModel? = null
        @Synchronized
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            field = LooprViewModelFactory.get(this, wallet)
            return field
        }

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderDetailsLooprComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setTitle(R.string.order_details)

        orderSummaryViewModel?.getOrderByHash(this, orderHash) {
            when {
                adapter != null -> adapter?.orderSummary = it
                else -> setupAdapter(view, it)
            }
            bindLooprOrder(it)
        }
        setupOfflineFirstStateAndErrorObserver(orderSummaryViewModel, orderDetailsSwipeRefreshLayout)
    }

    // MARK - Private Methods

    private fun setupAdapter(view: View, looprOrder: LooprOrder) {
        val adapter = OrderDetailsAdapter(looprOrder).let {
            adapter = it
            return@let it
        }

        // We can now retrieve order fills, since we initialized the adapter with the order data
        orderFillsViewModel?.getOrderFills(this, orderHash) {
            setupOfflineFirstDataObserverForAdapter(orderFillsViewModel, adapter, it)
        }
        setupOfflineFirstStateAndErrorObserver(orderFillsViewModel, orderDetailsSwipeRefreshLayout)

        orderDetailsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        orderDetailsRecyclerView.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun bindLooprOrder(order: LooprOrder) {

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
            OrderFilter.FILTER_OPEN_NEW -> {
                orderDetailsOrderProgressView.visibility = View.VISIBLE
                orderDetailsOrderProgressView.progress = 0

                orderDetailsOrderCompleteImage.visibility = View.GONE
            }
            OrderFilter.FILTER_OPEN_PARTIAL -> {
                orderDetailsOrderProgressView.visibility = View.VISIBLE
                orderDetailsOrderProgressView.progress = order.percentageFilled

                orderDetailsOrderCompleteImage.visibility = View.GONE
            }
            OrderFilter.FILTER_FILLED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_swap_horiz_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
            OrderFilter.FILTER_CANCELLED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_cancel_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
            OrderFilter.FILTER_EXPIRED -> {
                orderDetailsOrderCompleteImage.visibility = View.VISIBLE
                orderDetailsOrderCompleteImage.setImageResource(R.drawable.ic_alarm_white_24dp)

                orderDetailsOrderProgressView.visibility = View.GONE
            }
        }

        // Token Amount
        val numberFormatter = currencySettings.getNumberFormatter()
        orderDetailsTokenAmountLabel.text = "${numberFormatter.format(order.amount)} $primaryTicker"

        // Fiat Amount
        val currencyFormatter = currencySettings.getCurrencyFormatter()
        orderDetailsFiatAmountLabel.text = currencyFormatter.format(order.priceInUsd)

        if(order.isComplete) {
            orderDetailsSwipeRefreshLayout.isEnabled = false
        } else {
            orderDetailsSwipeRefreshLayout.isEnabled = true
        }

    }

}