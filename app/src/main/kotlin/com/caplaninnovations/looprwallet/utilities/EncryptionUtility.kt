package com.caplaninnovations.looprwallet.utilities

import java.security.SecureRandom

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object EncryptionUtility {

    fun getNextRandomKey(): ByteArray {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

}