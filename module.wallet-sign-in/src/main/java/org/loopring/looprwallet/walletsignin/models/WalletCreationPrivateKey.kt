package org.loopring.looprwallet.walletsignin.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Corey Caplan on 4/18/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WalletCreationPrivateKey(override val walletName: String) : WalletCreationResult, Parcelable