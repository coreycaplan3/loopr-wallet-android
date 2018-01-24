package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 1/19/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class OpenOrdersFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_open, container, false)
    }

}