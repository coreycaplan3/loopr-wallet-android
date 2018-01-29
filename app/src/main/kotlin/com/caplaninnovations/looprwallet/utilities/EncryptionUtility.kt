package com.caplaninnovations.looprwallet.utilities

import kotlin.experimental.and

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object EncryptionUtility {

    private val hexArray = "0123456789ABCDEF".toCharArray()

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val number = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[number.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[(number and 0x0F)]
        }
        return String(hexChars)
    }

}