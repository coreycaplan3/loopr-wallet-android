package org.loopring.looprwallet.createtransfer.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_create_transfer_amount.*
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.contacts.repositories.contacts.ContactsRepository
import org.loopring.looprwallet.createtransfer.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.settings.EthereumFeeSettingsFragment
import org.loopring.looprwallet.core.presenters.NumberPadPresenter
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate.Companion.MAX_CURRENCY_FRACTION_DIGITS
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate.Companion.MAX_EXCHANGE_RATE_FRACTION_DIGITS
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprWalletViewModelFactory
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.core.viewmodels.eth.EthTokenBalanceViewModel
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTransactionViewModel
import org.loopring.looprwallet.core.viewmodels.price.EthTokenPriceCheckerViewModel
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
class CreateTransferAmountFragment : BaseFragment(), NumberPadPresenter.NumberPadActionListener {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_CURRENCY_AMOUNT = "_CURRENCY_AMOUNT"
        private const val KEY_TOKEN_AMOUNT = "_TOKEN_AMOUNT"

        private const val KEY_IS_CURRENCY_MAIN_LABEL = "_IS_CURRENCY_MAIN_LABEL"

        private const val KEY_RECIPIENT_ADDRESS = "_RECIPIENT_ADDRESS"

        fun createInstance(recipientAddress: String) =
                CreateTransferAmountFragment().apply {
                    arguments = bundleOf(KEY_RECIPIENT_ADDRESS to recipientAddress)
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
    @VisibleForTesting
    var isCurrencyMainLabel = false

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

    lateinit var address: String

    @VisibleForTesting
    lateinit var ethToken: EthToken

    @VisibleForTesting
    lateinit var currentToken: EthToken

    @VisibleForTesting
    var contact: Contact? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet()
            field = wallet?.let { ContactsRepository(it).getContactByAddressNow(recipientAddress) }
            return field
        }

    @VisibleForTesting
    val ethTokenPriceCheckerViewModel: EthTokenPriceCheckerViewModel by lazy {
        return@lazy LooprWalletViewModelFactory.get<EthTokenPriceCheckerViewModel>(this).apply {
            setupOfflineFirstStateAndErrorObserver(this, ::refreshAll)
        }
    }

    @VisibleForTesting
    var ethTokenBalanceViewModel: EthTokenBalanceViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null

            return LooprWalletViewModelFactory.get<EthTokenBalanceViewModel>(this, wallet)
                    .apply {
                        field = this
                        setupOfflineFirstStateAndErrorObserver(this, ::refreshAll)
                    }
        }

    @VisibleForTesting
    val ethereumTransactionViewModel by lazy {
        ViewModelProviders.of(this).get(EthereumTransactionViewModel::class.java)
    }

    @VisibleForTesting
    var tokenBalanceList = listOf<CryptoToken>()

    @VisibleForTesting
    val maxDecimalPlaces: Int
        get() = when {
            isCurrencyMainLabel -> 2
            else -> MAX_EXCHANGE_RATE_FRACTION_DIGITS
        }

    @VisibleForTesting
    lateinit var spinnerActionView: Spinner

    override val isDecimalVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyAmount = savedInstanceState?.getString(KEY_CURRENCY_AMOUNT) ?: "0"
        tokenAmount = savedInstanceState?.getString(KEY_TOKEN_AMOUNT) ?: "0"
        isCurrencyMainLabel = savedInstanceState?.getBoolean(KEY_IS_CURRENCY_MAIN_LABEL, true) != false

        address = walletClient.getCurrentWallet()?.credentials?.address ?: LooprWallet.WATCH_ONLY_WALLET.credentials.address

        ethToken = ethTokenBalanceViewModel?.getEthBalanceNow() ?: EthToken.ETH
        currentToken = ethTokenPriceCheckerViewModel.currentCryptoToken ?: ethToken

        toolbar?.title = null
        toolbar?.subtitle = contact?.name ?: recipientAddress

        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        ethTokenBalanceViewModel?.getAllTokensWithBalances(this, address) {
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
            val balance = currentToken.getBalanceOf(address) ?: return@setOnClickListener
            val priceInNativeCurrency = currentToken.priceInNativeCurrency
                    ?: return@setOnClickListener

            // TODO calculate max, minus GAS settings (if sending ETH)
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

            when (currentToken.identifier) {
                EthToken.ETH.identifier -> onSendEth(bdTokenAmount, totalEtherTransferAmount)
                else -> onSendToken(bdTokenAmount, totalTokenTransferAmount)
            }
        }

        NumberPadPresenter.setupNumberPad(this, this)

        setupTokenTicker(currentToken)

        bindAmountsToText()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_create_transfer_amount, menu)

        val spinnerItem = menu.findItem(R.id.createTransferAmountSpinner)
        val spinner = (spinnerItem.actionView as Spinner)
        spinnerActionView = spinner

        val data = tokenBalanceList
        val adapterList: List<String> = data.map { it.ticker }
        val selectedIndex = adapterList.indexOfFirstOrNull { it == currentToken.ticker } ?: 0

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, adapterList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val balance = data[position].getBalanceOf(address)

                currencyAmount = "0"
                tokenAmount = "0"
                bindAmountsToText()

                setupTokenTicker(data[position])
                if (balance == null || balance.equalsZero() && ethTokenBalanceViewModel?.isLoading() == false) {
                    ethTokenBalanceViewModel?.refresh()
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
            else -> when {
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

    private fun refreshAll() {
        ethTokenPriceCheckerViewModel.refresh()
        ethTokenBalanceViewModel?.refresh()
    }

    /**
     * Sets up [EthTokenPriceCheckerViewModel] to watch an [EthToken].
     */
    private fun setupTokenTicker(token: CryptoToken) {
        ethTokenPriceCheckerViewModel.getTokenPrice(this, token.identifier) {
            currentToken = it

            val balance = it.getBalanceOf(address)?.formatAsToken(currencySettings, it.ticker)
                    ?: NEGATIVE_ONE
            createTransferBalanceLabel.text = when (balance) {
                NEGATIVE_ONE -> str(R.string.formatter_balance).format(balance)
                else -> ""
            }

            val price = it.priceInNativeCurrency?.formatAsCurrency(currencySettings)

            if (price != null) {
                val formattedPrice = str(R.string.formatter_current_ticker_price).format(it.ticker, price)
                createTransferCurrentPriceLabel?.text = formattedPrice
                bindAmountsToText()
            }
        }
    }

    /**
     * Called when a user is attempting to send ETH. This method checks for insufficient balance
     * issues.
     */
    private fun onSendEth(amountToSend: BigDecimal, totalTransactionCost: BigDecimal) {
        val ethBalance = ethToken.getBalanceOf(address) ?: NEGATIVE_ONE
        val gasLimit = ethereumFeeSettings.currentEthTransferGasLimit
        val gasPrice = ethereumFeeSettings.currentGasPrice
        when {
            ethBalance == NEGATIVE_ONE -> onEthBalanceError()
            ethBalance < (amountToSend + totalTransactionCost) ->
                view?.context?.longToast(R.string.cannot_send_more_eth_than_balance_and_gas)
            else ->
                buildSendConfirmationDialog {
                    val credentials = walletClient.getCurrentWallet()!!.credentials
                    ethereumTransactionViewModel.sendTokens(
                            currentToken.contractAddress,
                            credentials,
                            address,
                            amountToSend,
                            gasLimit,
                            gasPrice
                    )
                }
        }
    }

    private fun onSendToken(amountToSend: BigDecimal, totalTransactionCost: BigDecimal) {
        val tokenToSendBalance = currentToken.getBalanceOf(address) ?: NEGATIVE_ONE
        val ethBalance = ethToken.getBalanceOf(address) ?: NEGATIVE_ONE

        val gasLimit = ethereumFeeSettings.currentTokenTransferGasLimit
        val gasPrice = ethereumFeeSettings.currentGasPrice
        when {
            ethBalance == NEGATIVE_ONE -> onEthBalanceError()
            tokenToSendBalance == NEGATIVE_ONE -> {
                // This should not happen, since the button is disabled until a balance was loaded
                loge("Could not get the user\'s token balance!", IllegalStateException())
                view?.longSnackbarWithAction(R.string.error_retrieve_token_balance, R.string.retry) {
                    ethTokenBalanceViewModel?.refresh()
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
                buildSendConfirmationDialog {
                    val credentials = walletClient.getCurrentWallet()!!.credentials
                    ethereumTransactionViewModel.sendTokens(
                            currentToken.contractAddress,
                            credentials,
                            address,
                            amountToSend,
                            gasLimit,
                            gasPrice
                    )
                }
            }
        }
    }

    /**
     * Builds an [AlertDialog] that's used to display a confirmation to the user of whether or not
     * they want to send tokens/ETH.
     */
    private inline fun buildSendConfirmationDialog(crossinline onPositiveButtonClick: () -> Unit) {
        val context = context ?: return

        val currencyAmount = BigDecimal(currencyAmount).formatAsCurrency(currencySettings)
        val recipient = contact?.name ?: address
        val message = str(R.string.formatter_confirm_token_send).format(currentToken.ticker, currencyAmount, recipient)

        AlertDialog.Builder(context)
                .setTitle(R.string.question_send_tokens)
                .setMessage(message)
                .setPositiveButton(R.string.send) { _, _ ->
                    (activity as? BaseActivity)?.progressDialog?.apply {
                        setMessage(getString(R.string.sending))
                        show()
                    }
                    onPositiveButtonClick()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    private fun bindAmountsToText() {
        val ticker = currentToken.ticker
        when {
            isCurrencyMainLabel -> {
                createTransferMainLabel.text = currencyAmount.formatAsCustomCurrency(currencySettings)

                val priceInNativeCurrency = currentToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the token
                // IE --> $500 / $1,000 (per ETH) = 0.5 tokens
                val bdTokenAmount = (BigDecimal(currencyAmount).setScale(8)) / (priceInNativeCurrency.setScale(8))
                tokenAmount = bdTokenAmount.setScale(8, RoundingMode.HALF_DOWN).toPlainString()
                createTransferSecondaryLabel.text = bdTokenAmount.formatAsToken(currencySettings, ticker)
            }
            else -> {
                createTransferMainLabel.text = tokenAmount.formatAsCustomToken(currencySettings, ticker)

                val priceInNativeCurrency = currentToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the currency
                // IE --> 0.5 (tokens) * $1,000 (per ETH) = $500
                val bdCurrencyAmount = BigDecimal(tokenAmount) * priceInNativeCurrency
                currencyAmount = bdCurrencyAmount.setScale(2, RoundingMode.HALF_DOWN).toPlainString()
                createTransferSecondaryLabel.text = bdCurrencyAmount.formatAsCurrency(currencySettings)
            }
        }

        val balance = currentToken.getBalanceOf(address)
        val bdTokenAmount = BigDecimal(tokenAmount)
        createTransferSendButton.isEnabled = balance != null && !bdTokenAmount.equalsZero()
    }

    /**
     * Gets either [currencyAmount] or [tokenAmount] depending on which one is currency the main
     * label.
     *
     * @see isCurrencyMainLabel
     */
    private fun getAmountBasedOnCurrencyMainLabel() = when {
        isCurrencyMainLabel -> currencyAmount
        else -> tokenAmount
    }

    private fun setAmountBasedOnCurrencyMainLabel(amount: String) = when {
        isCurrencyMainLabel -> currencyAmount = amount
        else -> tokenAmount = amount
    }

    override fun onOfflineFirstStateChange(viewModel: OfflineFirstViewModel<*, *>, state: Int) {
        updateUiBasedOnValidData()

        when {
            viewModel === ethTokenPriceCheckerViewModel -> when (state) {
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
        }
    }

    /**
     * Updates the swap, max, and number pad based on whether the two essential data providers,
     * [ethTokenBalanceViewModel] and [ethTokenPriceCheckerViewModel], have valid data.
     */
    private fun updateUiBasedOnValidData() {
        if (ethTokenBalanceViewModel?.hasValidData() == true && ethTokenPriceCheckerViewModel.hasValidData()) {
            createTransferSwapButton.isEnabled = true
            createTransferMaxButton.isEnabled = true
            NumberPadPresenter.enableNumberPad(this)
        } else {
            createTransferSwapButton.isEnabled = false
            createTransferMaxButton.isEnabled = false
            NumberPadPresenter.disableNumberPad(this)
        }
    }

    private fun onEthBalanceError() {
        // This should not happen, since the button is disabled until a balance was loaded
        loge("Could not get user\'s ETH balance!", IllegalStateException())
        view?.longSnackbarWithAction(R.string.error_retrieve_eth_balance, R.string.retry) {
            ethTokenBalanceViewModel?.refresh()
        }
    }

}