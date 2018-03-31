package com.caplaninnovations.looprwallet.fragments.transfers

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.utilities.NetworkUtility
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Corey on 3/22/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class CreateTransferAmountFragmentTest : BaseDaggerFragmentTest<CreateTransferAmountFragment>() {

    override val fragment = CreateTransferAmountFragment.createInstance("0x0123456701234567012345670123456701234567")
    override val tag = CreateTransferAmountFragment.TAG

    @Test
    fun transferEth_basedOnCurrency_insufficientBalance() {

    }

    @Test
    fun transferEth_basedOnCurrency_okayBalance() {

    }

    @Test
    fun transferEth_basedOnToken_insufficientBalance() {

    }

    @Test
    fun transferEth_basedOnToken_okayBalance() {

    }

    @Test
    fun transferToken_basedOnCurrency_insufficientBalance() {

    }

    @Test
    fun transferToken_basedOnCurrency_okayBalance() {

    }

    @Test
    fun transferToken_basedOnToken_insufficientBalance() {

    }

    @Test
    fun transferToken_basedOnToken_okayBalance() {

    }

    @Test
    fun noConnection_checkErrorStates() {
        // We are mocking not having a connection
        NetworkUtility.mockIsNetworkAvailable = false
    }

    @Test
    fun countIntegersBeforeDecimal() {
        assertEquals(1, fragment.getAmountBeforeDecimal("0.0000"))
        assertEquals(1, fragment.getAmountBeforeDecimal("0"))
        assertEquals(1, fragment.getAmountBeforeDecimal("0.101"))
        assertEquals(2, fragment.getAmountBeforeDecimal("12.1010"))
        assertEquals(5, fragment.getAmountBeforeDecimal("12345.0010"))
        assertEquals(0, fragment.getAmountBeforeDecimal("0.11"))
    }

    @Test
    fun countIntegersAfterDecimal() {
        assertEquals(3, fragment.getAmountAfterDecimal("0.0000"))
        assertEquals(0, fragment.getAmountAfterDecimal("0"))
        assertEquals(3, fragment.getAmountAfterDecimal("0.101"))
        assertEquals(4, fragment.getAmountAfterDecimal("12.1010"))
        assertEquals(4, fragment.getAmountAfterDecimal("12345.0010"))
        assertEquals(2, fragment.getAmountAfterDecimal("0.11"))
        assertEquals(1, fragment.getAmountAfterDecimal("0.1"))
    }

    // MARK - Private Methods

    /**
     * Changes the spinner in the toolbar and switches the currently selected CryptoToken to be a
     * token instead of ETH.
     */
    fun switchTransferFromEthToToken() {
    }

}