package org.loopring.looprwallet.hometransfers.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_view_transfers.*
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.extensions.setupWithFab
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.hometransfers.R
import org.loopring.looprwallet.hometransfers.adapters.OnTransferClickListener
import org.loopring.looprwallet.hometransfers.adapters.ViewTransfersAdapter
import org.loopring.looprwallet.hometransfers.viewmodels.ViewAllTransfersViewModel
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity
import org.loopring.looprwallet.transferdetails.dialogs.TransferDetailsDialog

/**
 * Created by Corey Caplan on 2/26/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersFragment : BaseFragment(), OnRefreshListener, OnTransferClickListener {

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

        viewTransfersSwipeRefresh.setOnRefreshListener(this)

        val adapter = ViewTransfersAdapter(this)
        setupOfflineFirstStateAndErrorObserver(viewAllTransfersViewModel, viewTransfersSwipeRefresh)
        viewAllTransfersViewModel?.getAllTransfers(this) {
            setupOfflineFirstDataObserverForAdapter(viewAllTransfersViewModel, adapter, it)
        }

        viewTransfersRecyclerView.layoutManager = LinearLayoutManager(view.context)
        viewTransfersRecyclerView.adapter = adapter
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_send_white_24dp)
        floatingActionButton.setOnClickListener {
            CreateTransferActivity.route(this)
        }
        viewTransfersRecyclerView.setupWithFab(floatingActionButton)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureActivity.handleActivityResult(requestCode, resultCode, data) { type, value ->
            when (type) {
                BarcodeCaptureActivity.TYPE_PUBLIC_KEY -> {
                    CreateTransferActivity.route(this, value)
                }
                BarcodeCaptureActivity.TYPE_TRADING_PAIR -> {
                    val tradingPair = TradingPair.createFromMarket(value)
                    TradingPairDetailsActivity.route(tradingPair, this)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
        R.id.menuMainScanQrCode -> {
            BarcodeCaptureActivity.route(this, arrayOf(BarcodeCaptureActivity.TYPE_PUBLIC_KEY, BarcodeCaptureActivity.TYPE_TRADING_PAIR))
            true
        }
        R.id.menuMainSettings -> {
            SettingsActivity.route(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewAllTransfersViewModel?.refresh()
    }

    override fun onTransferClick(looprTransfer: LooprTransfer) {
        TransferDetailsDialog.getInstance(looprTransfer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewTransfersRecyclerView?.clearOnScrollListeners()
    }

}