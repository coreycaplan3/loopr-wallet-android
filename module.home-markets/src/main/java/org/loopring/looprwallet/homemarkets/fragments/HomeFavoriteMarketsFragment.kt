package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.homemarkets.adapters.HomeMarketsAdapter

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeFavoriteMarketsFragment : BaseHomeChildMarketsFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_markets_favorites

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)!!

    override val recyclerView: RecyclerView
        get() = findViewById(R.id.fragmentContainer)!!

    override val isFavorites: Boolean = true

    override fun provideAdapter(savedInstanceState: Bundle?): HomeMarketsAdapter {
        return HomeMarketsAdapter(savedInstanceState, this, this, ::onRefresh)
    }

}