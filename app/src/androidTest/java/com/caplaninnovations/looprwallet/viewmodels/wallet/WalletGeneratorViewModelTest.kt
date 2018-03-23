package com.caplaninnovations.looprwallet.viewmodels.wallet

import com.caplaninnovations.looprwallet.R
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
            var error = getErrorMessageFromKeystoreError(CipherException("Unable to deserialize params: "))
            assertEquals(R.string.error_unknown, error)

            error = getErrorMessageFromKeystoreError(CipherException("Invalid password provided"))
            assertEquals(R.string.error_password_invalid, error)

            error = getErrorMessageFromKeystoreError(CipherException("Wallet version is not supported"))
            assertEquals(R.string.error_keystore_unsupported, error)

            error = getErrorMessageFromKeystoreError(CipherException("Blah blah blah"))
            assertEquals(R.string.error_unknown, error)
        }
    }

}