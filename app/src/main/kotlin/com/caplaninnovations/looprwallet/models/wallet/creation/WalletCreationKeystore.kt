package com.caplaninnovations.looprwallet.models.wallet.creation

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
data class WalletCreationKeystore(var walletName: String, var password: String) : Parcelable, PasswordBasedWallet {

    override fun getWalletPassword(): String {
        return password
    }

}