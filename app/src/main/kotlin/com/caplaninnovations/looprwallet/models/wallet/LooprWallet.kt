package com.caplaninnovations.looprwallet.models.wallet

import org.web3j.crypto.Credentials
import java.util.*

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
data class LooprWallet(val walletName: String, val realmKey: ByteArray,
                       private val privateKey: String) {

    val credentials: Credentials = Credentials.create(privateKey)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LooprWallet

        if (walletName != other.walletName) return false
        if (!Arrays.equals(realmKey, other.realmKey)) return false
        if (privateKey != other.privateKey) return false
        if (credentials != other.credentials) return false

        return true
    }

    override fun hashCode(): Int {
        var result = walletName.hashCode()
        result = 31 * result + Arrays.hashCode(realmKey)
        result = 31 * result + privateKey.hashCode()
        result = 31 * result + credentials.hashCode()
        return result
    }

}