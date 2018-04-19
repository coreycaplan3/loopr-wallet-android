package org.loopring.looprwallet.walletsignin.viewmodels

import org.loopring.looprwallet.core.R
import org.junit.Test

import org.junit.Assert.*
import org.web3j.crypto.CipherException

/**
 * Created by Corey on 2/25/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class WalletGeneratorViewModelTest {

    @Test
    fun getErrorMessageFromKeystoreError() {
        WalletGeneratorViewModel.Companion.apply {
            var error = getMessageFromError(CipherException("Unable to deserialize params: "))
            assertEquals(R.string.error_unknown, error)

            error = getMessageFromError(CipherException("Invalid password provided"))
            assertEquals(R.string.error_password_invalid, error)

            error = getMessageFromError(CipherException("Wallet version is not supported"))
            assertEquals(R.string.error_keystore_unsupported, error)

            error = getMessageFromError(CipherException("Blah blah blah"))
            assertEquals(R.string.error_unknown, error)
        }
    }

}