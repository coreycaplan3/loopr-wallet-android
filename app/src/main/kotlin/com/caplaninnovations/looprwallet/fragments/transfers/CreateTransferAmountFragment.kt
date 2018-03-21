package com.caplaninnovations.looprwallet.fragments.transfers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.*
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.repositories.user.ContactsRepository
import com.caplaninnovations.looprwallet.viewmodels.LooprWalletViewModelFactory
import com.caplaninnovations.looprwallet.viewmodels.OfflineFirstViewModel
import com.caplaninnovations.looprwallet.viewmodels.price.TokenPriceCheckerViewModel
import com.caplaninnovations.looprwallet.viewmodels.wallet.TokenBalanceViewModel
import kotlinx.android.synthetic.main.fragment_create_transfer_amount.*
import kotlinx.android.synthetic.main.number_pad.*
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
class CreateTransferAmountFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_CURRENCY_AMOUNT = "_CURRENCY_AMOUNT"
        private const val KEY_TOKEN_AMOUNT = "_TOKEN_AMOUNT"

        private const val KEY_IS_CURRENCY_MAIN_LABEL = "_IS_CURRENCY_MAIN_LABEL"

        private const val KEY_RECIPIENT_ADDRESS = "_RECIPIENT_ADDRESS"

        fun createInstance(recipientAddress: String): CreateTransferAmountFragment {
            return CreateTransferAmountFragment().apply {
                arguments = Bundle().apply { putString(KEY_RECIPIENT_ADDRESS, recipientAddress) }
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

    /**
     * The amount that the user has entered, in their native currency
     */
    private var currencyAmount: String = "0"

    /**
     * The amount that the user has entered, represented as a quantity of tokens
     */
    private var tokenAmount: String = "0"

    private lateinit var currentCryptoToken: CryptoToken

    private var currentState: Int = OfflineFirstViewModel.STATE_IDLE_EMPTY

    private val contact: Contact? by lazy {
        val wallet = walletClient.getCurrentWallet()
        wallet?.let { ContactsRepository(it).getContactByAddressNow(recipientAddress) }
    }

    private val tokenPriceCheckerViewModel: TokenPriceCheckerViewModel?
        get() {
            val wallet = walletClient.getCurrentWallet() ?: return null

            val model = LooprWalletViewModelFactory.get<TokenPriceCheckerViewModel>(this, wallet)
            model.addCurrentStateObserver(this) {
                if (model.isLoading()) {
                    progressBar?.visibility = View.VISIBLE
                } else {
                    progressBar?.visibility = View.INVISIBLE
                }

                currentState = it
                if (it == OfflineFirstViewModel.STATE_LOADING_EMPTY) {
                    createTransferCurrentPriceLabel?.text = getString(R.string.loading_price)
                    createTransferSecondaryLabel?.text = getString(R.string.loading_exchange_rate)
                } else if (it == OfflineFirstViewModel.STATE_IDLE_EMPTY) {
                    createTransferCurrentPriceLabel?.text = getString(R.string.error_loading_price)
                    createTransferSecondaryLabel?.text = getString(R.string.error_loading_exchange_rate)

                    createTransferSwapButton.isEnabled = false
                    createTransferMaxButton.isEnabled = false
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

    private val tokenBalanceViewModel: TokenBalanceViewModel?
        get() {
            val wallet = walletClient.getCurrentWallet() ?: return null
            val model = TokenBalanceViewModel(wallet)

            model.addErrorObserver(this) {
                view?.snackbarWithAction(
                        message = it.errorMessage,
                        actionText = R.string.reload,
                        length = Snackbar.LENGTH_INDEFINITE,
                        listener = { model.refresh() }
                )
            }

            model.addDataObserver(this) {
                logd("Valid token balance: ${it.size}")
            }

            return model
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LooprWalletApp.dagger.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyAmount = savedInstanceState?.getString(KEY_CURRENCY_AMOUNT) ?: "0"
        tokenAmount = savedInstanceState?.getString(KEY_TOKEN_AMOUNT) ?: "0"
        isCurrencyMainLabel = savedInstanceState?.getBoolean(KEY_IS_CURRENCY_MAIN_LABEL, true) != false

        toolbar?.title = contact?.name

        createTransferSwapButton.setOnClickListener {
            isCurrencyMainLabel = !isCurrencyMainLabel
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
                        .setScale(2, RoundingMode.HALF_DOWN).toPlainString()
            } else {
                // Calculate max based on token amount
                // MAX is just the balance
                tokenAmount = balance.setScale(8, RoundingMode.HALF_DOWN).toPlainString()
            }

            bindAmountsToText()
        }

        setupNumberPad()

        currentCryptoToken = tokenPriceCheckerViewModel?.currentCryptoToken ?: EthToken.ETH
        currentCryptoToken.priceInNativeCurrency?.let {
            val bdTokenAmount = BigDecimal(currencyAmount) / it
            tokenAmount = bdTokenAmount.toString()
        }

        setupTokenTicker(currentCryptoToken)

        bindAmountsToText()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_transfer_amount, menu)

        val item = menu.findItem(R.id.createTransferAmountSpinner)
        val spinner = item.actionView as Spinner

        val data = tokenBalanceViewModel?.data ?: listOf(EthToken.ETH, EthToken.LRC)
        val adapterList: List<String> = data.map { it.ticker }
        val selectedIndex = adapterList.indexOfFirstOrNull { it == currentCryptoToken.ticker } ?: 0

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, adapterList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setupTokenTicker(data[position])
            }
        }

        spinner.setSelection(selectedIndex)
    }

    private fun setupSpinnerAdapter() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_CURRENCY_AMOUNT, currencyAmount)
        outState.putString(KEY_TOKEN_AMOUNT, tokenAmount)
        outState.putBoolean(KEY_IS_CURRENCY_MAIN_LABEL, isCurrencyMainLabel)
    }

    // MARK - Private Methods

    private fun setupNumberPad() {
        // Dynamically adjust the user's decimal point to their native  one
        numberPadDecimal.text = currencySettings.getDecimalSeparator()

        numberPadDecimal.setOnClickListener(this::onKeypadClick)
        numberPadZero.setOnClickListener(this::onKeypadClick)
        numberPadBackspace.setOnClickListener(this::onKeypadClick)
        numberPadOne.setOnClickListener(this::onKeypadClick)
        numberPadTwo.setOnClickListener(this::onKeypadClick)
        numberPadThree.setOnClickListener(this::onKeypadClick)
        numberPadFour.setOnClickListener(this::onKeypadClick)
        numberPadFive.setOnClickListener(this::onKeypadClick)
        numberPadSix.setOnClickListener(this::onKeypadClick)
        numberPadSeven.setOnClickListener(this::onKeypadClick)
        numberPadEight.setOnClickListener(this::onKeypadClick)
        numberPadNine.setOnClickListener(this::onKeypadClick)
    }

    /**
     * Sets up [TokenPriceCheckerViewModel] to watch an [EthToken].
     */
    private fun setupTokenTicker(token: CryptoToken) {
        tokenPriceCheckerViewModel?.getTokenPrice(this, token.identifier) {
            currentCryptoToken = it

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

    private fun onKeypadClick(view: View) {
        var amount = if (isCurrencyMainLabel) {
            currencyAmount
        } else {
            tokenAmount
        }

        amount = when (view.id) {
            R.id.numberPadDecimal -> {
                if (!amount.contains(".")) {
                    "$amount."
                } else {
                    amount
                }
            }
            R.id.numberPadZero -> {
                appendNumber(amount, "0")
            }
            R.id.numberPadBackspace -> {
                if (amount.last() == '.') {
                    amount.substring(0, amount.length - 2)
                } else if (amount != "0") {
                    amount.substring(0, amount.length - 1)
                } else {
                    amount
                }
            }
            R.id.numberPadOne -> appendNumber(amount, "1")
            R.id.numberPadTwo -> appendNumber(amount, "2")
            R.id.numberPadThree -> appendNumber(amount, "3")
            R.id.numberPadFour -> appendNumber(amount, "4")
            R.id.numberPadFive -> appendNumber(amount, "5")
            R.id.numberPadSix -> appendNumber(amount, "6")
            R.id.numberPadSeven -> appendNumber(amount, "7")
            R.id.numberPadEight -> appendNumber(amount, "8")
            R.id.numberPadNine -> appendNumber(amount, "9")
            else -> throw IllegalArgumentException("Invalid view!")
        }

        if (amount.isEmpty()) {
            amount = "0"
        }

        if (isCurrencyMainLabel) {
            currencyAmount = amount
        } else {
            tokenAmount = amount
        }

        bindAmountsToText()
    }

    /**
     * @return The new string with the newly appended number
     */
    private fun appendNumber(amount: String, number: String): String {
        val amountBefore = amount.getAmountBeforeDecimal()
        val amountAfter = amount.getAmountAfterDecimal()
        val hasDecimal = amount.contains(".")

        if (number == "0") {
            return if (hasDecimal && amountAfter < getMaxDecimalPlaces()) {
                // Decimal that isn't using all possible decimal places
                "${amount}0"
            } else if (!hasDecimal && amount != "0" && amountBefore < CurrencyExchangeRate.maxWholeDigits) {
                // Whole number, it's not equal to 0, and we have less than the number of max digits
                "${amount}0"
            } else {
                amount
            }
        }

        return when {
            amount == "0" -> number
            !hasDecimal && amountBefore < CurrencyExchangeRate.maxWholeDigits -> "$amount$number"
            hasDecimal -> {
                if (isCurrencyMainLabel && amountAfter < CurrencyExchangeRate.maxCurrencyFractionDigits) {
                    "$amount$number"
                } else if (!isCurrencyMainLabel && amountAfter < CurrencyExchangeRate.maxExchangeRateFractionDigits) {
                    "$amount$number"
                } else {
                    amount
                }
            }
            else -> {
                amount
            }
        }
    }

    /**
     * Counts the number of digits before the decimal point. If there's no decimal point, it
     * counts all the digits
     */
    private fun String.getAmountBeforeDecimal(): Int {
        forEachIndexed { i, ch ->
            if (ch == '.') return i
        }
        return this.length
    }

    /**
     * Counts the number of digits after the decimal point. If there's no decimal point, 0 is
     * returned.
     */
    private fun String.getAmountAfterDecimal(): Int {
        var decimalFlag = false
        var counter = 0
        forEach { ch ->
            if (decimalFlag) counter += 1
            if (ch == '.') decimalFlag = true
        }
        return counter
    }

    private fun getMaxDecimalPlaces() =
            if (isCurrencyMainLabel) 2
            else CurrencyExchangeRate.maxExchangeRateFractionDigits

    private fun bindAmountsToText() {
        val ticker = currentCryptoToken.ticker
        when {
            isCurrencyMainLabel -> {
                createTransferMainLabel.text = BigDecimal(currencyAmount).formatAsCurrency(currencySettings)

                val priceInNativeCurrency = currentCryptoToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the token
                // IE --> $500 / $1,000 (per ETH) = 0.5 tokens
                val bdTokenAmount = BigDecimal(currencyAmount) / priceInNativeCurrency
                tokenAmount = bdTokenAmount.setScale(8, RoundingMode.HALF_DOWN).toString()
                createTransferSecondaryLabel.text = bdTokenAmount.formatAsToken(currencySettings, ticker)
            }
            else -> {
                createTransferMainLabel.text = BigDecimal(tokenAmount).formatAsToken(currencySettings, ticker)

                val priceInNativeCurrency = currentCryptoToken.priceInNativeCurrency ?: return

                // We're setting the secondary label in terms of the currency
                // IE --> 0.5 (tokens) * $1,000 (per ETH) = $500
                val bdCurrencyAmount = BigDecimal(tokenAmount) * priceInNativeCurrency
                currencyAmount = bdCurrencyAmount.setScale(2, RoundingMode.HALF_DOWN).toString()
                createTransferSecondaryLabel.text = bdCurrencyAmount.formatAsCurrency(currencySettings)
            }
        }
    }

}