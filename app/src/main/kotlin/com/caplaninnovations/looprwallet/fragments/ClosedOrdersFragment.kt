package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import kotlinx.android.synthetic.main.fragment_orders_closed.*

/**
 * Created by Corey Caplan on 1/19/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class ClosedOrdersFragment: BaseFragment() {

    override var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_closed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = closedOrdersContainer
    }

}