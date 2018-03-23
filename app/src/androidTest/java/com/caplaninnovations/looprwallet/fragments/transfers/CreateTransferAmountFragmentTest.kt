package com.caplaninnovations.looprwallet.fragments.transfers

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
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

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun stringExtension_trimExtraZeros() {
        assertEquals("0.", fragment.trimExtraZerosAfterDecimal("0.0000"))
        assertEquals("0", fragment.trimExtraZerosAfterDecimal("0"))
        assertEquals("0.101", fragment.trimExtraZerosAfterDecimal("0.101"))
        assertEquals("0.101", fragment.trimExtraZerosAfterDecimal("0.1010"))
        assertEquals("5.001", fragment.trimExtraZerosAfterDecimal("5.0010"))
        assertEquals("5.11", fragment.trimExtraZerosAfterDecimal("5.11"))
    }

    @Test
    fun stringExtension_countIntegersBeforeDecimal() {
        assertEquals(1, fragment.getAmountBeforeDecimal("0.0000"))
        assertEquals(1, fragment.getAmountBeforeDecimal("0"))
        assertEquals(1, fragment.getAmountBeforeDecimal("0.101"))
        assertEquals(2, fragment.getAmountBeforeDecimal("12.1010"))
        assertEquals(5, fragment.getAmountBeforeDecimal("12345.0010"))
        assertEquals(0, fragment.getAmountBeforeDecimal("0.11"))
    }

    @Test
    fun stringExtension_countIntegersAfterDecimal() {
        assertEquals(3, fragment.getAmountAfterDecimal("0.0000"))
        assertEquals(0, fragment.getAmountAfterDecimal("0"))
        assertEquals(3, fragment.getAmountAfterDecimal("0.101"))
        assertEquals(4, fragment.getAmountAfterDecimal("12.1010"))
        assertEquals(4, fragment.getAmountAfterDecimal("12345.0010"))
        assertEquals(2, fragment.getAmountAfterDecimal("0.11"))
        assertEquals(1, fragment.getAmountAfterDecimal("0.1"))
    }

}