package com.caplaninnovations.looprwallet.fragments.transfers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.os.bundleOf
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.*
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.settings.EthereumFeeSettingsFragment
import com.caplaninnovations.looprwallet.handlers.NumberPadHandler
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentTransactionController
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate.Companion.MAX_CURRENCY_FRACTION_DIGITS
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate.Companion.MAX_EXCHANGE_RATE_FRACTION_DIGITS
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.repositories.user.ContactsRepository
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import com.caplaninnovations.looprwallet.viewmodels.LooprWalletViewModelFactory
import com.caplaninnovations.looprwallet.viewmodels.OfflineFirstViewModel
import com.caplaninnovations.looprwallet.viewmodels.price.TokenPriceCheckerViewModel
import com.caplaninnovations.looprwallet.viewmodels.wallet.TokenBalanceViewModel
import kotlinx.android.synthetic.main.fragment_create_transfer_amount.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To perform the second (and final) part of a transfer event, which involves
 * entering the amount.
 */
class CreateTransferAmountFragment : BaseFragment(), NumberPadHandler.NumberPadActionListener {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_CURRENCY_AMOUNT = "_CURRENCY_AMOUNT"
        private const val KEY_TOKEN_AMOUNT = "_TOKEN_AMOUNT"

        private const val KEY_IS_CURRENCY_MAIN_LABEL = "_IS_CURRENCY_MAIN_LABEL"

        private const val KEY_RECIPIENT_ADDRESS = "_RECIPIENT_ADDRESS"

        fun createInstance(recipientAddress: String): CreateTransferAmountFragment {
            return CreateTransferAmountFragment().apply {
                arguments = bundleOf(KEY_RECIPIENT_ADDRESS to recipientAddress)
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_transfer_amount

    private val recipientAddress: String by lazy {
        arguments!!.getString(KEY_RECIPIENT_ADDRESS)
    }

    /**
     * True if the main label is the user's native currency or false if it's represented as a token
     * amount.
     *
     * If true, the secondary label is represented as token amount. If false, the secondary label
     * is the user's native currency.
     */
    private var isCurrencyMainLabel = false

    @Inject
    lateinit var currencySettings: CurrencySettings

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

    /**
     * The amount that the user has entered, in their native currency
     */
    private var currencyAmount: String = "0"

    /**
     * The amount that the user has entered, represented as a quantity of tokens
     */
    private var tokenAmount: String = "0"

    private lateinit var ethToken: EthToken
    private lateinit var currentCryptoToken: CryptoToken

    private var contact: Contact? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet()
            field = wallet?.let { ContactsRepository(it).getContactByAddressNow(recipientAddress) }
            return field
        }

    private var tokenPriceCheckerViewModel: TokenPriceCheckerViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null

            val model = LooprWalletViewModelFactory.get<TokenPriceCheckerViewModel>(this, wallet)
            field = model
            model.addCurrentStateObserver(this) {
                when {
                    model.isLoading() -> progressBar?.visibility = View.VISIBLE
                    else -> progressBar?.visibility = View.INVISIBLE
                }

                when (it) {
                    OfflineFirstViewModel.STATE_LOADING_EMPTY -> {
                        createTransferCurrentPriceLabel?.text = getString(R.string.loading_price)
                        createTransferSecondaryLabel?.text = getString(R.string.loading_exchange_rate)
                    }
                    OfflineFirstViewModel.STATE_IDLE_EMPTY -> {
                        createTransferCurrentPriceLabel?.text = getString(R.string.error_loading_price)
                        createTransferSecondaryLabel?.text = getString(R.string.error_loading_exchange_rate)

                        createTransferSwapButton.isEnabled = false
                        createTransferMaxButton.isEnabled = false
                    }
                }

                if (model.hasValidData()) {
                    createTransferSwapButton.isEnabled = true
                    createTransferMaxButton.isEnabled = true
                }
            }

            model.addErrorObserver(this) {
                view?.snackbar(it.errorMessage)
            }

            return model
        }

    private var tokenBalanceViewModel: TokenBalanceViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            val model = LooprWalletViewModelFactory.get<TokenBalanceViewModel>(this, wallet)
            field = model

            model.addCurrentStateObserver(this) {
                if (model.isLoading()) {

                }
            }

            model.addErrorObserver(this) {
                view?.snackbarWithAction(
                        message = it.errorMessage,
                        actionText = R.string.reload,
                        length = Snackbar.LENGTH_INDEFINITE,
                        listener = { model.refresh() }
                )
            }

            return model
        }

    private var tokenBalanceList = listOf<CryptoToken>()

    private val maxDecimalPlaces: Int
        get() = when {
            isCurrencyMainLabel -> 2
            else -> MAX_EXCHANGE_RATE_FRACTION_DIGITS
        }

    override val isDecimalVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LooprWalletApp.dagger.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyAmount = savedInstanceState?.getString(KEY_CURRENCY_AMOUNT) ?: "0"
        tokenAmount = savedInstanceState?.getString(KEY_TOKEN_AMOUNT) ?: "0"
        isCurrencyMainLabel = savedInstanceState?.getBoolean(KEY_IS_CURRENCY_MAIN_LABEL, true) != false

        ethToken = tokenBalanceViewModel?.getEthBalanceNow() ?: EthToken.ETH
        currentCryptoToken = tokenPriceCheckerViewModel?.currentCryptoToken ?: ethToken

        toolbar?.title = null
        toolbar?.subtitle = contact?.name ?: recipientAddress

        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        tokenBalanceViewModel?.getAllTokensWithBalances(this, address) {
            tokenBalanceList = it
            activity?.invalidateOptionsMenu()
        }

        createTransferSwapButton.setOnClickListener {
            isCurrencyMainLabel = !isCurrencyMainLabel

            currencyAmount = "0"
            tokenAmount = "0"

            bindAmountsToText()
        }

        createTransferMaxButton.setOnClickListener {
            val balance = currentCryptoToken.balance ?: return@setOnClickListener
            val priceInNativeCurrency = currentCryptoToken.priceInNativeCurrency
                    ?: return@setOnClickListener

            if (isCurrencyMainLabel) {
                // calculate max based on currency
                // MAX = balance * princeInCurrency
                currencyAmount = (balance * priceInNativeCurrency)
                        .setScale(2, RoundingMode.HALF_DOWN)
                        .toPlainString()
            } else {
                // Calculate max based on token amount
                // MAX is just the balance
                tokenAmount = balance.setScale(8, RoundingMode.HALF_DOWN)
                        .toPlainString()
            }

            bindAmountsToText()
        }

        createTransferSendButton.setOnClickListener {
            val bdTokenAmount = BigDecimal(tokenAmount)
            val gasPrice = ethereumFeeSettings.currentGasPrice
            val totalEtherTransferAmount = gasPrice * ethereumFeeSettings.currentEthTransferGasLimit
            val totalTokenTransferAmount = gasPrice * ethereumFeeSettings.currentTokenTransferGasLimit

            when (currentCryptoToken.identifier) {
                EthToken.ETH.identifier -> onSendEth(bdTokenAmount, totalEtherTransferAmount)
                else -> onSendToken(bdTokenAmount, totalTokenTransferAmount)
            }
        }

        NumberPadHandler.setupNumberPad(this, this)

        setupTokenTicker(currentCryptoToken)

        bindAmountsToText()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_create_transfer_amount, menu)

        val item = menu.findItem(R.id.createTransferAmountSpinner)
        val spinner = item.actionView as Spinner

        val data = tokenBalanceList
        val adapterList: List<String> = data.map { it.ticker }
        val selectedIndex = adapterList.indexOfFirstOrNull { it == currentCryptoToken.ticker } ?: 0

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, adapterList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val balance = data[position].balance

                currencyAmount = "0"
                tokenAmount = "0"
                bindAmountsToText()

                setupTokenTicker(data[position])
                if (balance == null || balance.equalsZero() && tokenBalanceViewModel?.isLoading() == false) {
                    tokenBalanceViewModel?.refresh()
                    spinner.context.longToast(R.string.error_send_tokens_with_zero_balance)
                }
            }
        }

        spinner.setSelection(selectedIndex)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_CURRENCY_AMOUNT, currencyAmount)
        outState.putString(KEY_TOKEN_AMOUNT, tokenAmount)
        outState.putBoolean(KEY_IS_CURRENCY_MAIN_LABEL, isCurrencyMainLabel)
    }

    /**
     * Counts the number of digits before the decimal point. If there's no decimal point, it
     * counts all the digits
     */
    fun getAmountBeforeDecimal(s: String): Int {
        s.forEachIndexed { i, ch ->
            if (ch == '.') return i
        }
        return s.length
    }

    /**
     * Counts the number of digits after the decimal point. If there's no decimal point, 0 is
     * returned.
     */
    fun getAmountAfterDecimal(s: String): Int {
        var decimalFlag = false
        var counter = 0
        s.forEach { ch ->
            if (decimalFlag) counter += 1
            if (ch == '.') decimalFlag = true
        }
        return counter
    }

    /**
     * @return The new string with the newly appended number
     */
    override fun onNumberClick(number: String) {
        var amount = getAmountBasedOnCurrencyMainLabel()
        val amountBefore = getAmountBeforeDecimal(amount)
        val amountAfter = getAmountAfterDecimal(amount)
        val hasDecimal = amount.contains(".")

        amount = when (number) {
            "0" -> when {
                hasDecimal && amountAfter < maxDecimalPlaces ->
                    // Decimal that isn't using all possible decimal places
                    "${amount}0"
                !hasDecimal && amount != "0" && amountBefore < CurrencyExchangeRate.MAX_INTEGER_DIGITS ->
                    // Whole number, the number isn't 0, and there's less than the number of fractional digits
                    "${amount}0"
                else ->
                    // Default to just returning the number itself, without mutations
                    amount
            }
            else ->
                when {
                    amount == "0" -> number
                    !hasDecimal && amountBefore < CurrencyExchangeRate.MAX_INTEGER_DIGITS ->
                        // Whole number and there's less than the number of integer digits
                        "$amount$number"
                    hasDecimal ->
                        when {
                            isCurrencyMainLabel && amountAfter < MAX_CURRENCY_FRACTION_DIGITS ->
                                // Currency decimal with less than the max currency digits
                                "$amount$number"
                            !isCurrencyMainLabel && amountAfter < MAX_EXCHANGE_RATE_FRACTION_DIGITS ->
                                // Token decimal with less than the max token digits
                                "$amount$number"
                            else ->
                                // Default to just returning the number itself, without mutations
                                amount
                        }
                    else ->
                        // Default to returning the number itself, without mutations
                        amount
                }
        }

        if (amount.isEmpty()) {
            amount = "0"
        }

        setAmountBasedOnCurrencyMainLabel(amount)

        bindAmountsToText()
    }

    override fun onBackspaceClick() {
        val amount = getAmountBasedOnCurrencyMainLabel()
        if (amount != "0") {
            setAmountBasedOnCurrencyMainLabel(amount.substring(0, amount.length - 1))
        }
    }

    override fun onDecimalClick() {
        val amount = getAmountBasedOnCurrencyMainLabel()
        if (!amount.contains(".")) {
            setAmountBasedOnCurrencyMainLabel("$amount.")
        }
    }

    // MARK - Private Methods

    /**
     * Sets up [TokenPriceCheckerViewModel] to watch an [EthToken].
     */
    private fun setupTokenTicker(token: CryptoToken) {
        tokenPriceCheckerViewModel?.getTokenPrice(this, token.identifier) {
            currentCryptoToken = it

            val balance = it.balance?.formatAsToken(currencySettings, it.ticker) ?: NEGATIVE_ONE
            createTransferBalanceLabel.text = when (balance) {
                NEGATIVE_ONE -> String.format(str(R.string.formatter_balance), balance)
                else -> ""
            }

            val price = it.priceInNativeCurrency?.formatAsCurrency(currencySettings)

            if (price != null) {
                createTransferCurrentPriceLabel?.text = String.format(
                        getString(R.string.current_ticker_price),
                        it.ticker,
                        price)

                bindAmountsToText()
            }
        }
    }

    /**
     * Called when a user is attempting to send ETH. This method checks for insufficient balance
     * issues.
     */
    private fun onSendEth(amountToSend: BigDecimal, totalTransactionCost: BigDecimal) {
        val ethBalance = ethToken.balance ?: NEGATIVE_ONE
        when {
            ethBalance == NEGATIVE_ONE -> onEthBalanceError()
            ethBalance < (amountToSend + totalTransactionCost) ->
                view?.context?.longToast(R.string.cannot_send_more_eth_than_balance_and_gas)
            else -> {
                // TODO initiate ETH send
            }
        }
    }

    private fun onSendToken(amountToSend: BigDecimal, totalTransactionCost: BigDecimal) {
        val tokenToSendBalance = currentCryptoToken.balance ?: NEGATIVE_ONE
        val ethBalance = ethToken.balance ?: NEGATIVE_ONE
        when {
            ethBalance == NEGATIVE_ONE -> onEthBalanceError()
            tokenToSendBalance == NEGATIVE_ONE -> {
                // This should not happen, since the button is disabled until a balance was loaded
                loge("Could not get the user\'s token balance!", IllegalStateException())
                view?.longSnackbarWithAction(R.string.error_retrieve_token_balance, R.string.retry) {
                    tokenBalanceViewModel?.refresh()
                }
            }
            ethBalance < totalTransactionCost -> view?.longSnackbarWithAction(R.string.error_insufficient_gas, R.string.gas_settings) {
                val fragment = EthereumFeeSettingsFragment()
                val tag = EthereumFeeSettingsFragment.TAG

                FragmentTransactionController(R.id.activityContainer, fragment, tag)
                        .apply {
                            slideUpAndDownAnimation()
                            commitTransaction(requireFragmentManager())
                        }
            }
            tokenToSendBalance < amountToSend -> {
                view?.context?.longToast(R.string.error_insufficient_token_balance)
            }
            else -> {
                // TODO initiate send
            }
        }
    }

    private fun bindAmountsToText() {
        val ticker = currentCryptoToken.ticker
        when {
            isCurrencyMainLabel -> {
                createTransferMainLabel.text = currencyAmount.formatAsCustomCurrency(currencySettings)

                val priceInNativeCurrency = currentCryptoToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the token
                // IE --> $500 / $1,000 (per ETH) = 0.5 tokens
                val bdTokenAmount = (BigDecimal(currencyAmount).setScale(8)) / (priceInNativeCurrency.setScale(8))
                tokenAmount = bdTokenAmount.setScale(8, RoundingMode.HALF_DOWN).toPlainString()
                createTransferSecondaryLabel.text = bdTokenAmount.formatAsToken(currencySettings, ticker)
            }
            else -> {
                createTransferMainLabel.text = tokenAmount.formatAsCustomToken(currencySettings, ticker)

                val priceInNativeCurrency = currentCryptoToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the currency
                // IE --> 0.5 (tokens) * $1,000 (per ETH) = $500
                val bdCurrencyAmount = BigDecimal(tokenAmount) * priceInNativeCurrency
                currencyAmount = bdCurrencyAmount.setScale(2, RoundingMode.HALF_DOWN).toPlainString()
                createTransferSecondaryLabel.text = bdCurrencyAmount.formatAsCurrency(currencySettings)
            }
        }

        val balance = currentCryptoToken.balance
        val bdTokenAmount = BigDecimal(tokenAmount)
        createTransferSendButton.isEnabled = balance != null && !bdTokenAmount.equalsZero()
    }

    /**
     * Gets either [currencyAmount] or [tokenAmount] depending on which one is currency the main
     * label.
     */
    private fun getAmountBasedOnCurrencyMainLabel() = when {
        isCurrencyMainLabel -> currencyAmount
        else -> tokenAmount
    }

    private fun setAmountBasedOnCurrencyMainLabel(amount: String) = when {
        isCurrencyMainLabel -> currencyAmount = amount
        else -> tokenAmount = amount
    }

    private fun onEthBalanceError() {
        // This should not happen, since the button is disabled until a balance was loaded
        loge("Could not get user\'s ETH balance!", IllegalStateException())
        view?.longSnackbarWithAction(R.string.error_retrieve_eth_balance, R.string.retry) {
            tokenBalanceViewModel?.refresh()
        }
    }

}