package org.loopring.looprwallet.hometransfers.fragments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_view_transfers.*
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.setupWithFab
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.hometransfers.R
import org.loopring.looprwallet.hometransfers.viewmodels.ViewAllTransfersViewModel

/**
 * Created by Corey Caplan on 2/26/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersFragment : BaseFragment(), OnRefreshListener {

    override val layoutResource: Int
        get() = R.layout.fragment_view_transfers

    private var viewAllTransfersViewModel: ViewAllTransfersViewModel? = null
        @Synchronized
        get() {
            if (field != null) return field

            val currentWallet = walletClient.getCurrentWallet() ?: return null
            return LooprViewModelFactory.get<ViewAllTransfersViewModel>(this, currentWallet)
                    .apply {
                        field = this
                    }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfflineFirstStateAndErrorObserver(viewAllTransfersViewModel, ::onRefresh)

        viewTransfersRecyclerView.layoutManager = LinearLayoutManager(view.context)
//        viewTransfersRecyclerView.adapter =

        // TODO setup recycler view, view model, swipe refresh, onDetailsClick, error handling, etc.
        // TODO standardize BaseRealmRecyclerAdapter initialization with OfflineFirstViewModel :)
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_send_white_24dp)
        floatingActionButton.setOnClickListener {
            activity?.let { CreateTransferActivity.route(it) }
        }
        viewTransfersRecyclerView.setupWithFab(floatingActionButton)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewAllTransfersViewModel?.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewTransfersRecyclerView?.clearOnScrollListeners()
    }

}