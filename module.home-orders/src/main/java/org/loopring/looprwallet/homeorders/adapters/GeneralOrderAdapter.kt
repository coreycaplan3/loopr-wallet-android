package org.loopring.looprwallet.homeorders.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.guard
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.isSameDay
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_CANCELLED
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_DATES
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_FILLED
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_OPEN_ALL
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
 * @param orderType The order type to be displayed in this adapter.
 * @param activity The activity in which this adapter resides. The adapter stores a weak reference
 * to it for starting an instance of [OrderDetailsFragment] when necessary.
 * @param listener An instance of [OnGeneralOrderFilterChangeListener] for passing filter change
 * events to the caller.
 * @param cancelAllClickListener A listener for passing a click event to *cancel all orders* back
 * to the caller. This parameter should be **NOT** be null if the *orderType* is [FILTER_OPEN_ALL].
 *
 * @see OrderFilter.FILTER_OPEN_ALL
 * @see OrderFilter.FILTER_FILLED
 * @see OrderFilter.FILTER_CANCELLED
 */
class GeneralOrderAdapter(
        savedInstanceState: Bundle?,
        private val orderType: String,
        activity: BaseActivity,
        listener: OnGeneralOrderFilterChangeListener,
        cancelAllClickListener: (() -> Unit)? = null
) : BaseRealmAdapter<LooprOrder>(),
        OnGeneralOrderFilterChangeListener {

    companion object {
        private const val KEY_FILTER_DATE = "_FILTER_DATE"
        private const val KEY_FILTER_STATUS = "_FILTER_STATUS"
    }

    var currentDateFilter: String
        private set

    var currentOrderStatusFilter: String
        private set

    private val activity by weakReference(activity)
    private val listener by weakReference(listener)
    private val cancelAllClickListener by weakReference(cancelAllClickListener)

    override val totalItems: Int?
        get() = null

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

        currentDateFilter = savedInstanceState?.getString(KEY_FILTER_DATE) ?: FILTER_DATES[0]
        currentOrderStatusFilter = savedInstanceState?.getString(KEY_FILTER_STATUS) ?: orderType
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyGeneralOrderViewHolder(orderType, parent.inflate(R.layout.view_holder_general_orders_empty))
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return GeneralOrderViewHolder(parent.inflate(R.layout.view_holder_general_order))
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = when (orderType) {
        FILTER_OPEN_ALL -> {
            val view = parent.inflate(R.layout.view_holder_open_order_filter)
            GeneralOpenOrderFilterViewHolder(view, this, ::onCancelAllOpenOrdersClick)
        }
        FILTER_FILLED, FILTER_CANCELLED -> {
            val view = parent.inflate(R.layout.view_holder_closed_order_filter)
            GeneralClosedOrderFilterViewHolder(view, this)
        }
        else -> throw IllegalArgumentException("Invalid orderType, found: $orderType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprOrder?) {
        (holder as? EmptyGeneralOrderViewHolder)?.let {
            it.bind()
            return
        }

        (holder as? GeneralOpenOrderFilterViewHolder)?.let {
            it.bind(currentDateFilter, currentOrderStatusFilter)
            return
        }

        (holder as? GeneralClosedOrderFilterViewHolder)?.let {
            it.bind(currentDateFilter)
            return
        }

        // General Order Binding

        item?.guard { } ?: return

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

        (holder as? GeneralOrderViewHolder)?.bind(item, showDateHeader) {
            val activity = this.activity ?: return@bind

            OrderDetailsActivity.route(activity, it.orderHash)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCancelAllOpenOrdersClick(view: View) {
        cancelAllClickListener?.invoke()
    }

    override fun onStatusFilterChange(newOrderStatusFilter: String) {
        if (currentOrderStatusFilter != newOrderStatusFilter) {
            currentOrderStatusFilter = newOrderStatusFilter
            notifyItemChanged(0)
            listener?.onStatusFilterChange(newOrderStatusFilter)
        }
    }

    override fun onDateFilterChange(newDateFilter: String) {
        if (currentDateFilter != newDateFilter) {
            currentDateFilter = newDateFilter
            notifyItemChanged(0)
            listener?.onDateFilterChange(newDateFilter)
        }
    }

    override fun getCurrentDateFilterChange(): String {
        return currentDateFilter
    }

    override fun getCurrentStatusFilterChange(): String {
        return currentOrderStatusFilter
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_FILTER_DATE, currentDateFilter)
        outState.putString(KEY_FILTER_STATUS, currentOrderStatusFilter)
    }

}