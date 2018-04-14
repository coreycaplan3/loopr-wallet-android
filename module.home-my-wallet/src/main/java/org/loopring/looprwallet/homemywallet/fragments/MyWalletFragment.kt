package org.loopring.looprwallet.homemywallet.fragments

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.TooltipCompat
import android.view.View
import kotlinx.android.synthetic.main.card_wallet_information.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
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

        TooltipCompat.setTooltipText(shareAddressButton, str(R.string.share_your_address))
        TooltipCompat.setTooltipText(showPrivateKeyButton, str(R.string.reveal_private_key))

        val currentWallet = walletClient.getCurrentWallet()
        TooltipCompat.setTooltipText(shareAddressButton, str(R.string.viewkey))
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
    }

}