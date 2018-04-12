package org.loopring.looprwallet.homeorders.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.realm.RealmModel
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.isSameDay
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_CANCELLED
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_DATES
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_FILLED
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_OPEN_ALL
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.order.fragments.OrderDetailsFragment

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
) : BaseRealmAdapter<RealmModel>(),
        OnGeneralOrderFilterChangeListener {

    companion object {
        const val TYPE_FILTER = 3

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

        currentDateFilter = savedInstanceState?.getString(KEY_FILTER_DATE) ?: FILTER_DATES[0]
        currentOrderStatusFilter = savedInstanceState?.getString(KEY_FILTER_STATUS) ?: orderType
    }

    override fun getItemViewType(position: Int): Int {
        val type = super.getItemViewType(position)

        return when (type) {
            TYPE_DATA -> when (position) {
                0 -> TYPE_FILTER
                else -> TYPE_DATA
            }
            else -> type
        }
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyGeneralOrderViewHolder(orderType, parent)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        fun inflate(layoutRes: Int) = parent.inflate(layoutRes)

        return when (viewType) {
            TYPE_DATA -> GeneralOrderViewHolder(inflate(R.layout.view_holder_general_order))
            TYPE_FILTER -> when (orderType) {
                FILTER_OPEN_ALL -> {
                    val view = inflate(R.layout.view_holder_open_order_filter)
                    GeneralOpenOrderFilterViewHolder(view, this, ::onCancelAllOpenOrdersClick)
                }
                FILTER_FILLED, FILTER_CANCELLED -> {
                    val view = inflate(R.layout.view_holder_open_order_filter)
                    GeneralClosedOrderFilterViewHolder(view, this)
                }
                else -> throw IllegalArgumentException("Invalid orderType, found: $viewType")
            }
            else -> throw IllegalArgumentException("Invalid viewType, found: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: RealmModel) {
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

        val previousIndex = index - 1
        val previousItemIndex = index - 2 // There's an offset of 1 for the filter


        val showDateHeader = when {
            previousIndex == 0 ->
                // We're at the first item in the data-list
                true
            previousItemIndex >= 0 -> {
                val data = data
                // The item is NOT the SAME day as the previous one
                TODO("IMPLEMENT ME")
//                data != null && !data[previousItemIndex].lastUpdated.isSameDay(item.lastUpdated)
            }
            else -> false
        }

        (holder as? GeneralOrderViewHolder)?.bind(item, showDateHeader) {
            val activity = this.activity ?: return@bind

            val fragment = OrderDetailsFragment.getInstance("TODO") //TODO
            val tag = OrderDetailsFragment.TAG
            FragmentTransactionController(R.id.activityContainer, fragment, tag).apply {
                slideUpAndDownAnimation()
                commitTransaction(activity.supportFragmentManager)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCancelAllOpenOrdersClick(view: View) {
        cancelAllClickListener?.invoke()
    }

    override fun onStatusFilterChange(newStatusValue: String) {
        currentOrderStatusFilter = newStatusValue
        listener?.onStatusFilterChange(newStatusValue)
    }

    override fun onDateFilterChange(newDateValue: String) {
        currentDateFilter = newDateValue
        listener?.onDateFilterChange(newDateValue)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_FILTER_DATE, currentDateFilter)
        outState.putString(KEY_FILTER_STATUS, currentOrderStatusFilter)
    }

}