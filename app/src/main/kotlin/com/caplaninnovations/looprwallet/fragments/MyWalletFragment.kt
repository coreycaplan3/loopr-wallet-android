package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.logd
import kotlinx.android.synthetic.main.fragment_my_wallet.*

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MyWalletFragment: BaseFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = myWalletContainer
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
    }

}