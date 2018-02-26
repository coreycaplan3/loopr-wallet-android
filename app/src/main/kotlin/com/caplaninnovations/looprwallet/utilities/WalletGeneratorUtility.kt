package com.caplaninnovations.looprwallet.utilities

import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.viewmodels.WalletGeneratorViewModel

/**
 *  Created by Corey on 2/25/2018.
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 */
object WalletGeneratorUtility {

    /**
     * Setups and standardizes the usage of [WalletGeneratorViewModel]. This includes the observers
     * used to watch for wallet generation.
     *
     * @param walletGeneratorViewModel The generator used to create/restore wallets
     * @param fragment The fragment in which this view model resides
     */
    fun setupForFragment(walletGeneratorViewModel: WalletGeneratorViewModel,
                         fragment: Fragment) {
        val activity = fragment.activity
        walletGeneratorViewModel.observeIsCreationRunning(fragment, {
            val progress = (activity as? BaseActivity)?.progressDialog
            if (it == true) {
                progress?.setMessage(str(R.string.creating_wallet))
                progress?.show()
            } else {
                if (progress?.isShowing == true) {
                    progress.dismiss()
                }
            }
        })

        walletGeneratorViewModel.observeIsWalletCreated(fragment, {
            if (it == true) {
                activity?.startActivity(MainActivity.createIntent())
                activity?.finish()
            }
        })

        walletGeneratorViewModel.observeErrorMessage(fragment, {
            if (it != null) {
                fragment.view?.snackbar(it)
            }
        })
    }

}