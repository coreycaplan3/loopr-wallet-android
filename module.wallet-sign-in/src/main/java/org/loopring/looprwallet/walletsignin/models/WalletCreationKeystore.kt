package org.loopring.looprwallet.walletsignin.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WalletCreationKeystore(override val walletName: String, val password: String, val keystoreContent: String)
    : Parcelable, PasswordBasedWallet, WalletCreationResult {

    override fun getWalletPassword(): String {
        return password
    }

}