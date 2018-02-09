package com.caplaninnovations.looprwallet.utilities

/**
 * Created by Corey Caplan on 2/9/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */
object BlockchainUtility {

    fun isAddressValid(address: String) = "^0x[a-fA-F0-9]{40}\$".toRegex().matches(address)

}