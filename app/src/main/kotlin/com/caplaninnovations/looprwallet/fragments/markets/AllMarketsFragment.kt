package com.caplaninnovations.looprwallet.fragments.markets

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.adapters.AllMarketsAdapter
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_markets_all.*

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class AllMarketsFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_markets_all

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentContainer.layoutManager = LinearLayoutManager(context)
        fragmentContainer.adapter = AllMarketsAdapter()
    }

}