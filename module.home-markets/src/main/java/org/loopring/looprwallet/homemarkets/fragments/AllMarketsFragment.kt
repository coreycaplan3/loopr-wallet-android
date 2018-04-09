package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_markets_all.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.homemarkets.R

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
    }

}