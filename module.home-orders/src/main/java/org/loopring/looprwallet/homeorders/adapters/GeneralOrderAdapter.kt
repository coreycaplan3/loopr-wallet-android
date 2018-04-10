package org.loopring.looprwallet.homeorders.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.adapters.SavableAdapter
import org.loopring.looprwallet.core.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.isSameDay
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.utilities.ApplicationUtility.strArray
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 4/6/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display orders that appear on the home screen to the user
 *
 * @param isOpen True if this adapter will be showing open orders or false if it'll be past ones.
 */
class GeneralOrderAdapter(private val isOpen: Boolean, activity: BaseActivity) : BaseRealmAdapter<EthToken>(),
        OnGeneralOrderFilterActionListener, SavableAdapter {

    companion object {
        const val TYPE_FILTER = 3

        private const val KEY_FILTER_DATE = "_FILTER_DATE"
        private const val KEY_FILTER_STATUS = "_FILTER_STATUS"

        val FILTER_DATES: Array<String> = strArray(R.array.filter_order_dates)
        val FILTER_OPEN_ORDER_STATUS: Array<String> = strArray(R.array.filter_order_open_statuses)
        val FILTER_CLOSED_ORDER_STATUS: Array<String> = strArray(R.array.filter_order_closed_statuses)
    }

    lateinit var currentDateFilter: String
    lateinit var currentStatusFilter: String

    private val activity by weakReference(activity)

    override val totalItems: Int?
        get() = null

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        currentDateFilter = savedInstanceState?.getString(KEY_FILTER_DATE) ?: FILTER_DATES[0]

        val orderStatuses = when (isOpen) {
            true -> FILTER_OPEN_ORDER_STATUS
            else -> FILTER_CLOSED_ORDER_STATUS
        }
        currentStatusFilter = savedInstanceState?.getString(KEY_FILTER_STATUS) ?: orderStatuses[0]
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
        return EmptyGeneralOrderViewHolder(isOpen, parent)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GeneralOrderViewHolder(parent.inflate(R.layout.view_holder_general_order))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: EthToken) {
        (holder as? EmptyGeneralOrderViewHolder)?.let {
            it.bind()
            return
        }

        (holder as? GeneralOpenOrderFilterViewHolder)?.let {
            it.bind(currentDateFilter, currentStatusFilter)
            return
        }

        (holder as? GeneralClosedOrderFilterViewHolder)?.let {
            it.bind(currentDateFilter, currentStatusFilter)
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
                data != null && !data[previousItemIndex].lastUpdated.isSameDay(item.lastUpdated)
            }
            else -> false
        }

        (holder as? GeneralOrderViewHolder)?.bind(item, showDateHeader) {
            activity?.supportFragmentManager
            TODO("Add order details dialog!")
        }
    }

    override fun onStatusFilterChange(newStatusValue: String) {
        currentStatusFilter = newStatusValue
    }

    override fun onDateFilterChange(newDateValue: String) {
        currentDateFilter = newDateValue
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_FILTER_DATE, currentDateFilter)
        outState.putString(KEY_FILTER_STATUS, currentStatusFilter)
    }

}