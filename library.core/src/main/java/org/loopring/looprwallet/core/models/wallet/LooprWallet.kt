package org.loopring.looprwallet.core.models.wallet

import org.web3j.crypto.Credentials
import java.util.*

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a wallet being used by the user when signed in.
 *
 */
data class LooprWallet(
        val walletName: String,
        val realmKey: ByteArray,
        val keystoreContent: String?,
        val passphrase: Array<String>?,
        private val privateKey: String
) {

    companion object {

        private const val privateKey = "0123456701234567012345670123456701234567012345670123456701234567"

        val WATCH_ONLY_WALLET = LooprWallet(
                "watch-only",
                ByteArray(0),
                null,
                null,
                privateKey
        )
    }

    val credentials: Credentials = Credentials.create(privateKey)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LooprWallet

        if (walletName != other.walletName) return false
        if (!Arrays.equals(realmKey, other.realmKey)) return false
        if (keystoreContent != other.keystoreContent) return false
        if (!Arrays.equals(passphrase, other.passphrase)) return false
        if (privateKey != other.privateKey) return false
        if (credentials != other.credentials) return false

        return true
    }

    override fun hashCode(): Int {
        var result = walletName.hashCode()
        result = 31 * result + Arrays.hashCode(realmKey)
        result = 31 * result + (keystoreContent?.hashCode() ?: 0)
        result = 31 * result + (passphrase?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + privateKey.hashCode()
        result = 31 * result + credentials.hashCode()
        return result
    }


}