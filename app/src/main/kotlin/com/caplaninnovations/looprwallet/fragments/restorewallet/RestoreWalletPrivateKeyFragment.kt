package com.caplaninnovations.looprwallet.fragments.restorewallet

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment

/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class RestoreWalletPrivateKeyFragment : BaseFragment() {

    companion object {
        val TAG: String = RestoreWalletPrivateKeyFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_private_key

}