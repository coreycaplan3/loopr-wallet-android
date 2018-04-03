package org.loopring.looprwallet.walletsignin.models.wallet

/**
 * Created by Corey on 2/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An interface signifying that a wallet contains a password, in order to be
 * created
 *
 */
interface PasswordBasedWallet {

    fun getWalletPassword(): String
}