package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletSettings(context: Context) : LooprSettingsManager(context) {

    companion object {

        private const val KEY_CURRENT_WALLET = "_CURRENT_WALLET"
    }

    /**
     * @return The current wallet being used by a user. Null means there is no current wallet and
     * the user must select or recover one
     */
    fun getCurrentWalletUser(): String? {
        return getString(KEY_CURRENT_WALLET)
    }

    /**
     * @param newWallet The new wallet to be currently used. Can be null to "logout" from the
     * wallet.
     */
    fun changeCurrentWallet(newWallet: String?) {
        putString(KEY_CURRENT_WALLET, newWallet)
    }

}