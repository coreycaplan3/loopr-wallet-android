package org.loopring.looprwallet.hometransfers.fragments

import android.support.design.widget.FloatingActionButton
import android.view.MenuItem
import kotlinx.android.synthetic.main.fragment_view_transfers.*
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.setupWithFab
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.hometransfers.R

/**
 * Created by Corey Caplan on 2/26/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_view_transfers

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_send_white_24dp)
        floatingActionButton.setOnClickListener { logd("FAB CLICKED 2!") }
        fragmentContainer.setupWithFab(floatingActionButton)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentContainer.clearOnScrollListeners()
    }

}