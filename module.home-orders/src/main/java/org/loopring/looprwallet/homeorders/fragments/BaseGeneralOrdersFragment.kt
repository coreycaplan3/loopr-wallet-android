package org.loopring.looprwallet.homeorders.fragments

import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.RecyclerView
import android.widget.ArrayAdapter
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility.strArray
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseGeneralOrdersFragment : BaseFragment() {

    abstract val recyclerView: RecyclerView

    fun setDateFilterSpinner(spinner: AppCompatSpinner) {
        val dates = strArray(R.array.filter_order_dates)
        spinner.adapter = ArrayAdapter<String>(spinner.context, android.R.layout.simple_spinner_item, dates)
    }

}