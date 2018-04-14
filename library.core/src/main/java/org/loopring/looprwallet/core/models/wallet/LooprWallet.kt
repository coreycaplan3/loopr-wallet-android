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
        val WATCH_ONLY_WALLET = LooprWallet("watch-only", ByteArray(0), privateKey)
    }

    val credentials: Credentials = Credentials.create(privateKey)

}