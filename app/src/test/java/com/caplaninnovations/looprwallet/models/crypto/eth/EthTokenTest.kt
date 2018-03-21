package com.caplaninnovations.looprwallet.models.crypto.eth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthTokenTest {

    private val contractAddress = "0xef68e7c694f40c8202821edf525de3782458639f"
    private val ticker = "LRC"
    private val totalSupply = "1395076054523857892274603100"
    private val decimalPlaces = 18
    private val priceInUsd = BigDecimal("80.40").setScale(2)
    private val priceInNativeCurrency = BigDecimal("100.80").setScale(2)

    private lateinit var loopringToken: EthToken

    @Before
    fun setUp() {
        loopringToken = EthToken(
                contractAddress,
                ticker,
                totalSupply,
                decimalPlaces,
                priceInUsd,
                priceInNativeCurrency
        )
    }

    @Test
    fun getIdentifier() {
        assertEquals(contractAddress, loopringToken.identifier)
    }

    @Test
    fun getTotalSupply() {
        val formattedString = loopringToken.totalSupply.toString()
        assertEquals("1395076054523857892274603100", formattedString)
    }

    @Test
    fun setTotalSupply() {
        val newSupply = "246203093000000000000000000"
        loopringToken.totalSupply = BigDecimal(newSupply)

        assertEquals(BigDecimal(newSupply), loopringToken.totalSupply)
    }

    @Test
    fun getPriceInUsd() {
        assertNotNull(loopringToken.priceInUsd)

        val price = NumberFormat.getCurrencyInstance(Locale.US).format(loopringToken.priceInUsd)
        assertEquals("$80.40", price)
    }

    @Test
    fun setPriceInUsd() {
        assertNotNull(loopringToken.priceInUsd)

        loopringToken.priceInUsd = BigDecimal("45.87")

        val price = NumberFormat.getCurrencyInstance(Locale.US).format(loopringToken.priceInUsd)
        assertEquals("$45.87", price)
    }

    @Test
    fun getContractAddress() {
        assertEquals(contractAddress, loopringToken.contractAddress)
    }

    @Test
    fun getTicker() {
        assertEquals(ticker, loopringToken.ticker)
    }

    @Test
    fun getDecimalPlaces() {
        assertEquals(decimalPlaces, loopringToken.decimalPlaces)
    }

}