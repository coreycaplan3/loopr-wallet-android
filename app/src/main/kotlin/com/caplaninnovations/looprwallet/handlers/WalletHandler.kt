package com.caplaninnovations.looprwallet.handlers

import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class WalletHandler(private val settings: WalletSettings) {

    init {
        settings.getAllWallets()
    }

}