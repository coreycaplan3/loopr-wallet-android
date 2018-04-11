package org.loopring.looprwallet.homeorders.fragments

import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey on 4/11/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment in a set of tabs for displaying the user's cancelled orders
 */
class HomeCancelledOrdersFragment: BaseHomeOrdersFragment() {

    override val recyclerView: RecyclerView
        get() = TODO("not implemented") // TODO

    override fun provideAdapter(): GeneralOrderAdapter {
        TODO("not implemented") // TODO
    }

    override val layoutResource: Int
        get() = TODO("not implemented") // TODO

    override fun onQueryTextChangeListener(searchQuery: String) {
        TODO("not implemented") // TODO
    }
}