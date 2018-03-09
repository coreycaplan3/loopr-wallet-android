package com.caplaninnovations.looprwallet.fragments.transfers

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.logd
import com.caplaninnovations.looprwallet.utilities.setupWithFab
import kotlinx.android.synthetic.main.fragment_view_transfers.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_send_white_24dp)
        floatingActionButton.setOnClickListener { logd("FAB CLICKED 2!") }
        fragmentContainer.setupWithFab(floatingActionButton)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentContainer.clearOnScrollListeners()
    }

}