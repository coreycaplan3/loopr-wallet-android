package com.caplaninnovations.looprwallet.utilities

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Corey Caplan on 2/9/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class BlockchainUtilityTest {

    @Test
    fun isAddressValid() {
        val validAddress = "0xdd36CAf39bf610757eb081554C49d0e1f442F920"
        assertTrue(BlockchainUtility.isAddressValid(validAddress))

        val invalidAddressMissing0x = "dd36CAf39bf610757eb081554C49d0e1f442F920"
        assertFalse(BlockchainUtility.isAddressValid(invalidAddressMissing0x))

        val invalidAddressTooLong = "0xdd36CAf39bf610757eb081554C49d0e1f442F920abf"
        assertFalse(BlockchainUtility.isAddressValid(invalidAddressTooLong))

        val invalidAddressBadCharacters = "0xss36CAf39bf610757eb081554C49d0e1f442F920"
        assertFalse(BlockchainUtility.isAddressValid(invalidAddressBadCharacters))
    }

}