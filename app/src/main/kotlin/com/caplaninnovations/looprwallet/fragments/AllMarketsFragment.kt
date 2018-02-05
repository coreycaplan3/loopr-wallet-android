package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import kotlinx.android.synthetic.main.fragment_markets_all.*

/**
 * Created by Corey Caplan on 1/19/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class AllMarketsFragment : BaseFragment() {

    override var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_markets_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = allMarketsContainer
    }

}