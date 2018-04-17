package org.loopring.looprwallet.walletsignin.models.wallet

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param walletName The wallet's name, as inputted by the user
 * @param password The user-generated password for the wallet, to be used with the phrase for
 * recovery
 * @param phrase The auto-generated 12-word phrase
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WalletCreationPhrase(val walletName: String, val password: String, val phrase: ArrayList<String>) : Parcelable, PasswordBasedWallet {

    override fun getWalletPassword(): String {
        return password
    }
}