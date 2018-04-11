package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.adapters.SavableAdapter
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide *base* behavior for the **open** and **closed** orders fragments.
 */
abstract class BaseHomeOrdersFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener {

    abstract val recyclerView: RecyclerView

    lateinit var adapter: GeneralOrderAdapter
        private set

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = provideAdapter().apply {
            onRestoreInstanceState(savedInstanceState)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }

    abstract fun provideAdapter(): GeneralOrderAdapter

    final override fun onBottomNavigationReselected() {
        recyclerView.smoothScrollToPosition(0)
    }

    final override fun onQueryTextGainFocus() {
        // NO OP
    }

    final override fun onSearchItemExpanded() {
        // NO OP
    }

    final override fun onSearchItemCollapsed() {
        // NO OP
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        adapter.onSaveInstanceState(outState)
    }

}