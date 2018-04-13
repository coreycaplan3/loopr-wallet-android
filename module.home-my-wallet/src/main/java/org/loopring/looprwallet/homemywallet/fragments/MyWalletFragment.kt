package org.loopring.looprwallet.homemywallet.fragments

import android.os.Bundle
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.homemywallet.R

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MyWalletFragment: BaseFragment(), BottomNavigationPresenter.BottomNavigationReselectedLister {

    override val layoutResource: Int
        get() = R.layout.fragment_my_wallet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableToolbarCollapsing()
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
    }

}