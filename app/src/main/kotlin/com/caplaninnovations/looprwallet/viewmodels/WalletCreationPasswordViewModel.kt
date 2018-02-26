package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class WalletCreationPasswordViewModel : ViewModel() {

    val walletCreationPassword = MutableLiveData<WalletCreationKeystore>()

}