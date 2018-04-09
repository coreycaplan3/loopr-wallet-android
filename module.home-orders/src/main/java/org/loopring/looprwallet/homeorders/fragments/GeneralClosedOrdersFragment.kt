package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralClosedOrdersFragment : BaseFragment(), SearchViewPresenter.OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_closed

    private lateinit var searchViewPresenter: SearchViewPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = true,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )
    }

    override fun onQueryTextGainFocus() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChangeListener(searchQuery: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchItemExpanded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchItemCollapsed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchViewPresenter.onSaveInstanceState(outState)
    }

}