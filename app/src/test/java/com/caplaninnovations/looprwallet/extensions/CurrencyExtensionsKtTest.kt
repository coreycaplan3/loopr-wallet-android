package com.caplaninnovations.looprwallet.extensions

import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSecureSettingsDebugImpl
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

/**
 * Created by Corey on 3/22/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(MockitoJUnitRunner::class)
class CurrencyExtensionsKtTest {

    private val looprSettings = LooprSecureSettingsDebugImpl()

    private lateinit var settings: CurrencySettings

    @Before
    fun setup() {
        settings = CurrencySettings(looprSettings)
    }

    @After
    fun tearDown() {
        looprSettings.clear()
    }

    @Test
    fun equalsZero() {
        assertTrue(BigDecimal("0.000000").equalsZero())

        assertFalse(BigDecimal("0.000001").equalsZero())

        assertTrue(BigDecimal(0.00).equalsZero())
    }

    @Test
    fun formatAsCustomCurrency() {
        assertEquals("$0.", "0.".formatAsCustomCurrency(settings))
        assertEquals("$0.00", "0.00".formatAsCustomCurrency(settings))
        assertEquals("$0.", "0.".formatAsCustomCurrency(settings))
        assertEquals("$10.", "10.".formatAsCustomCurrency(settings))
        assertEquals("$123.", "123.".formatAsCustomCurrency(settings))
    }

    @Test
    fun formatAsCustomToken() {
        val ticker = "ETH"
        assertEquals("0. $ticker", "0.".formatAsCustomToken(settings, ticker))
        assertEquals("0.00 $ticker", "0.00".formatAsCustomToken(settings, ticker))
        assertEquals("0. $ticker", "0.".formatAsCustomToken(settings, ticker))
        assertEquals("10. $ticker", "10.".formatAsCustomToken(settings, ticker))
        assertEquals("123. $ticker", "123.".formatAsCustomToken(settings, ticker))
    }

    @Test
    fun formatAsCurrency() {
        assertEquals("$1", BigDecimal("1.").formatAsCurrency(settings))
        assertEquals("$1", BigDecimal("1.0").formatAsCurrency(settings))
        assertEquals("$1.00", BigDecimal("1.001").formatAsCurrency(settings))
        assertEquals("$1.01", BigDecimal("1.010").formatAsCurrency(settings))

        assertEquals("$100", BigDecimal("100").formatAsCurrency(settings))
        assertEquals("$1.05", BigDecimal("1.05").formatAsCurrency(settings))
        assertEquals("$0.99", BigDecimal("0.99").formatAsCurrency(settings))
    }

    @Test
    fun formatAsToken() {
        val ticker = "ETH"
        assertEquals("1.00 $ticker", BigDecimal("1.").formatAsToken(settings, ticker))
        assertEquals("1.00 $ticker", BigDecimal("1.0").formatAsToken(settings, ticker))
        assertEquals("1.001 $ticker", BigDecimal("1.001").formatAsToken(settings, ticker))
        assertEquals("1.01 $ticker", BigDecimal("1.010").formatAsToken(settings, ticker))

        assertEquals("100.00 $ticker", BigDecimal("100").formatAsToken(settings, ticker))
        assertEquals("1.00 $ticker", BigDecimal("1").formatAsToken(settings, ticker))
    }

    @Test
    fun isIntegerValue() {
        assertFalse(BigDecimal("1.001").isIntegerValue())
        assertFalse(BigDecimal("1.010").isIntegerValue())

        assertTrue(BigDecimal("1.0").isIntegerValue())
        assertTrue(BigDecimal("1.").isIntegerValue())
        assertTrue(BigDecimal("100").isIntegerValue())
        assertTrue(BigDecimal("1").isIntegerValue())
    }

}