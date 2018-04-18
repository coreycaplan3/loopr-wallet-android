package org.loopring.looprwallet.createtransfer.fragments

import android.arch.lifecycle.Observer
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import kotlinx.android.synthetic.main.fragment_create_transfer_amount.*
import kotlinx.android.synthetic.main.number_pad.*
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.withTimeout
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.presenters.NumberPadPresenter
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal


/**
 * Created by Corey on 3/22/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CreateTransferAmountFragmentTest : BaseDaggerFragmentTest<CreateTransferAmountFragment>() {

    companion object {
        const val name = "Daniel"
    }

    override fun provideFragment() = CreateTransferAmountFragment.getInstance(address)
    override val tag = CreateTransferAmountFragment.TAG

    @Before
    fun setup() {
        createRealm().use {
            it.executeTransaction { it.upsert(Contact(address, name)) }
        }
    }

    @Test
    fun contactReceivedByArguments_checkToolbarText() {
        assertEquals(name, fragment.contact?.name)
        assertEquals(name, fragment.toolbar?.subtitle)
    }

    // Default ETH balance for mocking is 100
    // Default token balance for mocking is 250

    @Test
    fun transferEth_basedOnCurrency_useMaxButton_insufficientBalance() = runBlockingUiCode {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        // We now have a valid balances loaded

        Espresso.onView(`is`(fragment.createTransferMaxButton))
                .perform(click())

        // The MAX amount should be $5000/ETH * 100ETH = $500,000
        assertEquals("$500,000", fragment.createTransferMainLabel.text.toString())
        assertEquals("100 ETH", fragment.createTransferSecondaryLabel.text.toString())

        // We're going to have an insufficient balance because we need a little ETH for gas

        checkSnackbarDisplayedWithText(R.string.cannot_send_more_eth_than_balance_and_gas)
    }

    @Test
    fun transferEth_basedOnCurrency_okayBalance() = runBlockingUiCode {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        // The amount should be $5000/ETH * 80ETH = $400,000
        Espresso.onView(`is`(fragment.numberPadFour)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())

        assertEquals("$400,000", fragment.createTransferMainLabel.text.toString())
        assertEquals("80 ETH", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSendData()
    }

    @Test
    fun transferEth_basedOnToken_useMaxButton_insufficientBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        Espresso.onView(`is`(fragment.createTransferSwapButton))
                .perform(click())

        assertFalse(fragment.isCurrencyMainLabel)

        Espresso.onView(`is`(fragment.createTransferMaxButton))
                .perform(click())

        // The MAX amount should be $5000/ETH * 100ETH = $500,000
        assertEquals("$500,000", fragment.createTransferMainLabel.text.toString())
        assertEquals("100 ETH", fragment.createTransferSecondaryLabel.text.toString())

        // We're going to have an insufficient balance because we need a little ETH for gas

        checkSnackbarDisplayedWithText(R.string.cannot_send_more_eth_than_balance_and_gas)
    }

    @Test
    fun transferEth_basedOnToken_okayBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        Espresso.onView(`is`(fragment.createTransferSwapButton))
                .perform(click())

        assertFalse(fragment.isCurrencyMainLabel)

        // The amount should be $5000/ETH * 80ETH = $400,000
        Espresso.onView(`is`(fragment.numberPadEight)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())

        assertEquals("80 ETH", fragment.createTransferMainLabel.text.toString())
        assertEquals("$400,000", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSendData()
    }

    @Test
    fun transferToken_basedOnCurrency_insufficientBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        switchTransferFromEthToLrcToken()

        // The amount should be $100/LRC * 251LRC = $25,100
        Espresso.onView(`is`(fragment.numberPadTwo)).perform(click())
        Espresso.onView(`is`(fragment.numberPadFive)).perform(click())
        Espresso.onView(`is`(fragment.numberPadOne)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())

        assertEquals("$25,100", fragment.createTransferMainLabel.text.toString())
        assertEquals("251 LRC", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSnackbarDisplayedWithText(R.string.error_insufficient_token_balance)
    }

    /**
     * We need ETH to send tokens.
     */
    @Test
    fun transferToken_basedOnCurrency_insufficientEthBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        switchTransferFromEthToLrcToken()

        // We have "no" ETH
        fragment.ethToken.tokenBalances.first()!!.balance = BigDecimal.ZERO

        Espresso.onView(`is`(fragment.createTransferMaxButton)).perform(click())

        assertEquals("$25,000", fragment.createTransferMainLabel.text.toString())
        assertEquals("250 LRC", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSnackbarDisplayedWithText(R.string.error_insufficient_gas)

    }

    @Test
    fun transferToken_basedOnCurrency_okayBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        switchTransferFromEthToLrcToken()

        Espresso.onView(`is`(fragment.createTransferMaxButton)).perform(click())

        assertEquals("$25,000", fragment.createTransferMainLabel.text.toString())
        assertEquals("250 LRC", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSendData()
    }

    /**
     * *basedOnToken* uses token input as the primary input instead of native currency
     */
    @Test
    fun transferToken_basedOnToken_insufficientBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        switchTransferFromEthToLrcToken()

        Espresso.onView(`is`(fragment.createTransferSwapButton))
                .perform(click())

        // The amount should be $100/LRC * 251LRC = $25,100
        Espresso.onView(`is`(fragment.numberPadTwo)).perform(click())
        Espresso.onView(`is`(fragment.numberPadFive)).perform(click())
        Espresso.onView(`is`(fragment.numberPadOne)).perform(click())

        assertEquals("251 LRC", fragment.createTransferMainLabel.text.toString())
        assertEquals("$25,100", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSnackbarDisplayedWithText(R.string.error_insufficient_token_balance)

        assertFalse(fragment.isCurrencyMainLabel)
    }

    /**
     * *basedOnToken* uses token input as the primary input instead of native currency
     */
    @Test
    fun transferToken_basedOnToken_okayBalance() {
        waitForBalancesToLoad()
        waitForPriceToLoad()
        checkNormalUIState()

        switchTransferFromEthToLrcToken()

        Espresso.onView(`is`(fragment.createTransferSwapButton))
                .perform(click())

        // The amount should be $100/LRC * 250LRC = $25,000
        Espresso.onView(`is`(fragment.numberPadTwo)).perform(click())
        Espresso.onView(`is`(fragment.numberPadFive)).perform(click())
        Espresso.onView(`is`(fragment.numberPadZero)).perform(click())

        assertEquals("250 LRC", fragment.createTransferMainLabel.text.toString())
        assertEquals("$25,000", fragment.createTransferSecondaryLabel.text.toString())

        Espresso.onView(`is`(fragment.createTransferSendButton))
                .perform(click())

        checkSendData()
    }

    @Test
    fun noConnection_checkErrorStates() = runBlockingUiCode {
        // We are mocking not having a connection
        NetworkUtility.mockIsNetworkAvailable = false

        val deferred = CompletableDeferred<Boolean>()
        fragment.ethTokenBalanceViewModel!!.addCurrentStateObserver(fragment) {
            val viewModel = fragment.ethTokenBalanceViewModel!!
            if (!viewModel.isLoading() && !viewModel.hasValidData()) {
                deferred.complete(true)
            }
        }

        withTimeout(BuildConfig.DEFAULT_NETWORK_TIMEOUT) { assertTrue(deferred.await()) }

        checkNoConnectionState()
    }

    @Test
    fun checkMaxDecimalPlaces() {
        fragment.isCurrencyMainLabel = true
        assertEquals(2, fragment.maxDecimalPlaces)

        fragment.isCurrencyMainLabel = false
        assertEquals(CurrencyExchangeRate.MAX_EXCHANGE_RATE_FRACTION_DIGITS, fragment.maxDecimalPlaces)
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

    private fun waitForBalancesToLoad() = runBlockingUiCode {
        val deferred = CompletableDeferred<Boolean>()
        fragment.ethTokenBalanceViewModel!!.addCurrentStateObserver(fragment) {
            val viewModel = fragment.ethTokenBalanceViewModel!!
            if (!viewModel.isLoading() && viewModel.hasValidData()) {
                deferred.complete(true)
            }
        }

        withTimeout(BuildConfig.DEFAULT_NETWORK_TIMEOUT) { assertTrue(deferred.await()) }
    }

    private fun waitForPriceToLoad() = runBlockingUiCode {
        val deferred = CompletableDeferred<Boolean>()
        fragment.ethTokenPriceCheckerViewModel!!.addCurrentStateObserver(fragment) {
            val viewModel = fragment.ethTokenBalanceViewModel!!
            if (!viewModel.isLoading() && viewModel.hasValidData()) {
                deferred.complete(true)
            }
        }

        withTimeout(BuildConfig.DEFAULT_NETWORK_TIMEOUT) { assertTrue(deferred.await()) }
    }

    /**
     * Checks the state of the UI after loading successfully from the network. This includes the
     * token's balance, price and number pad state
     */
    private fun checkNormalUIState() {
        val token = fragment.currentToken

        assertTrue(fragment.createTransferSwapButton.isEnabled)
        assertTrue(fragment.createTransferMaxButton.isEnabled)

        assertTrue(NumberPadPresenter.isNumberPadEnabled(fragment))

        val balance = token.getBalanceOf(address)!!.formatAsToken(fragment.currencySettings, token.ticker)
        val formattedBalance = str(R.string.formatter_balance).format(balance)
        Espresso.onView(`is`(fragment.createTransferBalanceLabel))
                .check(matches(withText(formattedBalance)))

        val price = token.priceInUsd!!.formatAsCurrency(fragment.currencySettings)
        val formattedPrice = str(R.string.formatter_current_ticker_price).format(price)
        Espresso.onView(`is`(fragment.createTransferCurrentPriceLabel))
                .check(matches(withText(formattedPrice)))
    }

    /**
     * Checks the state of the UI after loading successfully from the network. This includes the
     * token's balance, price and number pad state
     */
    private fun checkNoConnectionState() {
        assertFalse(fragment.createTransferSwapButton.isEnabled)
        assertFalse(fragment.createTransferMaxButton.isEnabled)

        assertTrue(NumberPadPresenter.isNumberPadDisabled(fragment))

        Espresso.onView(`is`(fragment.createTransferCurrentPriceLabel))
                .check(matches(withText(R.string.error_loading_price)))

        Espresso.onView(`is`(fragment.createTransferSecondaryLabel))
                .check(matches(withText(R.string.error_loading_exchange_rate)))

        Espresso.onView(`is`(fragment.createTransferBalanceLabel))
                .check(matches(withText("")))

        checkSnackbarDisplayedWithText(R.string.error_no_connection)
    }

    private fun checkSendData() = runBlockingUiCode {
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        assertTrue(activity.progressDialog.isShowing)

        val deferred = CompletableDeferred<TransactionReceipt>()
        fragment.ethereumTransactionViewModel.result().observe(fragment, Observer<TransactionReceipt> {
            if (it != null) {
                deferred.complete(it)
            }
        })

        val result = withTimeout(BuildConfig.DEFAULT_NETWORK_TIMEOUT) { deferred.await() }
        assertNotNull(result)
    }

    private fun checkSnackbarDisplayedWithText(@StringRes textResource: Int) {
        Espresso.onView(withText(textResource))
                .inRoot(withDecorView(not(`is`(activity.window.decorView))))
                .check(matches(isDisplayed()))
    }

    /**
     * Changes the spinner in the toolbar and switches the currently selected CryptoToken to be a
     * token instead of ETH.
     */
    private fun switchTransferFromEthToLrcToken() {
        Espresso.onView(`is`(fragment.spinnerActionView))
                .perform(click())

        val spinnerText = EthToken.LRC.ticker

        onData(allOf(`is`(instanceOf(String::class.java)), `is`(spinnerText)))
                .perform(click())

        Espresso.onView(`is`(fragment.spinnerActionView))
                .check(matches(withSpinnerText(spinnerText)))
    }

}