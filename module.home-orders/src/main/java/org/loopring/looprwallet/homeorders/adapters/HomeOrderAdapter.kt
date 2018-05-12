package org.loopring.looprwallet.homeorders.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.isSameDay
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_CANCELLED
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_FILLED
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_OPEN_ALL
import org.loopring.looprwallet.core.models.loopr.orders.OrderLooprPager
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.orderdetails.activities.OrderDetailsActivity
import org.loopring.looprwallet.orderdetails.fragments.OrderDetailsFragment

/**
 * Created by Corey Caplan on 4/6/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display orders that appear on the home screen to the user
 *
 * @param savedInstanceState The implementor's savedInstanceState which is used to restore some of
 * the visual state of this adapter.
 * @param address The address of the user whose orders are being viewed.
 * @param orderType The order type to be displayed in this adapter.
 * @param activity The activity in which this adapter resides. The adapter stores a weak reference
 * to it for starting an instance of [OrderDetailsFragment] when necessary.
 * @param listener An instance of [OnHomeOrderFilterChangeListener] for passing filter change
 * events to the caller.
 * @param cancelAllClickListener A listener for passing a click event to *cancel all orders* back
 * to the caller. This parameter should be **NOT** be null if the *orderType* is [FILTER_OPEN_ALL].
 *
 * @see OrderFilter.FILTER_OPEN_ALL
 * @see OrderFilter.FILTER_FILLED
 * @see OrderFilter.FILTER_CANCELLED
 */
class HomeOrderAdapter(
        savedInstanceState: Bundle?,
        address: String,
        private val orderType: String,
        activity: BaseActivity,
        listener: OnHomeOrderFilterChangeListener,
        cancelAllClickListener: (() -> Unit)? = null
) : BaseRealmAdapter<LooprOrder>(),
        OnHomeOrderFilterChangeListener {

    companion object {

        private const val KEY_ORDER_FILTER = "ORDER_FILTER"
    }

    override val currentStatusFilter: String
        get() = orderType

    private val activity by weakReference(activity)
    private val listener by weakReference(listener)
    private val cancelAllClickListener by weakReference(cancelAllClickListener)

    init {

    }

    val orderFilter: OrderFilter = savedInstanceState?.getParcelable(KEY_ORDER_FILTER) ?: OrderFilter(address, null, orderType, 1)

    override var pager: LooprAdapterPager<LooprOrder> = OrderLooprPager(orderFilter)

    init {

        when (orderType) {
            FILTER_OPEN_ALL -> {
                if (cancelAllClickListener == null) {
                    throw IllegalStateException("cancelAllClickListener cannot be null for FILTER_OPEN_ALL order type")
                }
            }
            FILTER_FILLED, FILTER_CANCELLED -> Unit
            else -> throw IllegalArgumentException("Invalid orderType, found: $orderType")
        }

        containsHeader = true
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_general_orders_empty, parent, false)
        return EmptyHomeOrderViewHolder(orderType, view)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_general_order, parent, false)
        return HomeOrderViewHolder(view)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view: View
        return when (orderType) {
            FILTER_OPEN_ALL -> {
                view = inflater.inflate(R.layout.view_holder_open_order_filter, parent, false)
                HomeOpenOrderFilterViewHolder(view, ::onCancelAllOpenOrdersClick)
            }
            FILTER_FILLED, FILTER_CANCELLED -> {
                view = inflater.inflate(R.layout.view_holder_closed_order_filter, parent, false)
                HomeClosedOrderFilterViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid orderType, found: $orderType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprOrder?) {
        (holder as? EmptyHomeOrderViewHolder)?.let {
            it.bind()
            return
        }

        (holder as? HomeOpenOrderFilterViewHolder)?.let {
            it.bind()
            return
        }

        (holder as? HomeClosedOrderFilterViewHolder)?.let {
            it.bind()
            return
        }

        // General Order Binding

        item ?: return // GUARD

        val previousIndex = index - 1
        val previousItemIndex = index - 2 // There's an offset of 1 for the filter

        val showDateHeader = when {
            previousIndex == 0 ->
                // We're at the first item in the data-list
                true
            previousItemIndex >= 0 -> {
                val data = data
                // If the item is NOT the SAME day as the previous one
                data != null && !data[previousItemIndex].orderDate.isSameDay(item.orderDate)
            }
            else -> false
        }

        (holder as? HomeOrderViewHolder)?.bind(item, showDateHeader) {
            val activity = this.activity ?: return@bind

            OrderDetailsActivity.route(activity, it.orderHash)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCancelAllOpenOrdersClick(view: View) {
        cancelAllClickListener?.invoke()
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_ORDER_FILTER, orderFilter)
    }

}