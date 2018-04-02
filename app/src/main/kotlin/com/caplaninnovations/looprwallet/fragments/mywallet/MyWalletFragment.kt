package com.caplaninnovations.looprwallet.fragments.mywallet

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import org.loopring.looprwallet.core.extensions.logd

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MyWalletFragment: BaseFragment(), BottomNavigationHandler.BottomNavigationReselectedLister {

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