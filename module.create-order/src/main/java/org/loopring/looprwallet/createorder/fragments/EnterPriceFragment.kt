package org.loopring.looprwallet.createorder.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_enter_price.*
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.presenters.TokenNumberPadPresenter
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.loopr.MarketsViewModel
import org.loopring.looprwallet.core.viewmodels.price.EthTokenPriceCheckerViewModel
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.viewmodels.CreateOrderViewModel
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by corey on 5/29/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class EnterPriceFragment : BaseFragment() {

    companion object {

        val TAG: String = EnterPriceFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_enter_price

    private val createOrderViewModel by lazy {
        LooprViewModelFactory.get<CreateOrderViewModel>(activity!!)
    }

    private val marketsViewModel by lazy {
        LooprViewModelFactory.get<MarketsViewModel>(activity!!)
    }

    private val tokenPriceViewModel by lazy {
        LooprViewModelFactory.get<EthTokenPriceCheckerViewModel>(activity!!)
    }

    private lateinit var numberPadPresenter: TokenNumberPadPresenter

    private var tradingPair: TradingPair? = null

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberPadPresenter = TokenNumberPadPresenter(this, enterPriceEnterPriceEditText, ::onFormChanged)

        createOrderViewModel.tradingPair.observe(fragmentViewLifecycleFragment!!, Observer {
            it?.let {
                tradingPair = it
                val token = it.primaryToken
                val ticker = token.ticker

                toolbar?.title = str(R.string.formatter_ticker_price).format(ticker)
                enterPriceExplainBodyLabel.text = str(R.string.formatter_amount_willing_to_pay_ticker).format(ticker)

                setupPriceViewModel(it)
                setupMarketsViewModel(it)
            }
        })

        onFormChanged()
        enterPriceConfirmButton.setOnClickListener(this::onSubmitClick)
    }

    override fun onFormChanged() {
        val value = enterPriceEnterPriceEditText.text.toString()
        enterPriceConfirmButton.isEnabled = TokenNumberPadPresenter.isValidTokenValue(value)

        bindCurrentEnteredPriceInFiat(tradingPair)
    }

    // MARK - Private Methods

    private fun setupMarketsViewModel(tradingPair: TradingPair) {
        val filter = TradingPairFilter(false, TradingPairFilter.CHANGE_PERIOD_1D)
        marketsViewModel.getMarkets(this, filter, TradingPairFilter.SORT_BY_TICKER_ASC) {
            val token = tradingPair.secondaryToken
            val text = str(R.string.formatter_current_price)
            val tokenPrice = tradingPair.lastPrice.formatAsToken(currencySettings, token)

            enterPriceCurrentPriceLabel.text = text.format(tokenPrice)
        }

        setupOfflineFirstStateAndErrorObserver(marketsViewModel, fragmentContainer)
    }

    private fun setupPriceViewModel(tradingPair: TradingPair) {

        val currency = currencySettings.getCurrency()
        tokenPriceViewModel.getTokenPrice(this, currency) {
            val identifier = tradingPair.primaryToken.identifier
            val currentFiatPrice = it.find { it.identifier == identifier }?.findCurrencyPrice(currency)?.price

            if (currentFiatPrice != null) {
                val text = str(R.string.formatter_estimated_current_price)
                val tokenPrice = currentFiatPrice.formatAsCurrency(currencySettings)
                enterPriceCurrentPriceFiatLabel.text = text.format(tokenPrice)
            }

            bindCurrentEnteredPriceInFiat(tradingPair)

        }

        setupOfflineFirstStateAndErrorObserver(tokenPriceViewModel, fragmentContainer)
    }

    private fun bindCurrentEnteredPriceInFiat(tradingPair: TradingPair?) {
        if (tradingPair == null) {
            enterPriceFiatLabel.text = str(R.string.loading_price)
            return
        }

        val estimatedPriceFormatter = str(R.string.formatter_estimated_price)

        val enteredPriceInTokens = enterPriceEnterPriceEditText.text.toString().toBigDecimalOrNull()
        if (enteredPriceInTokens == null) {
            val priceInFiat = BigDecimal.ZERO.formatAsCurrency(currencySettings)
            enterPriceFiatLabel.text = estimatedPriceFormatter.format(priceInFiat)
            return
        }

        val currency = currencySettings.getCurrency()
        val price = tradingPair.secondaryToken.findCurrencyPrice(currency)?.price
        if (price != null) {
            val priceInFiat = (price.toBigDecimal() * enteredPriceInTokens).formatAsCurrency(currencySettings)
            enterPriceFiatLabel.text = estimatedPriceFormatter.format(priceInFiat)
        } else {
            val priceInFiat = BigDecimal.ZERO.formatAsCurrency(currencySettings)
            enterPriceFiatLabel.text = estimatedPriceFormatter.format(priceInFiat)
        }

    }

    private fun onSubmitClick(view: View) {
        val button = view as Button
        val price = enterPriceEnterPriceEditText.text.toString().toBigDecimalOrNull()

        fun submitAndCloseFragment() {
            createOrderViewModel.priceLiveData.value = price
            popFragmentTransaction()
        }

        if (price != null) {
            val slippage = calculateSlippage(price)
            if (slippage >= BigDecimal(5)) {
                val slippageAsPercentage = "${slippage.toPlainString().first()}%"
                AlertDialog.Builder(view.context)
                        .setTitle(R.string.slippage_warning)
                        .setMessage(str(R.string.formatter_explain_slippage).format(slippageAsPercentage))
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                        .setPositiveButton(button.text) { dialog, _ ->
                            dialog.dismiss()
                            submitAndCloseFragment()
                        }
                        .show()
            } else {
                submitAndCloseFragment()
            }

        } else {
            view.snackbar(R.string.error_parse_price)
        }
    }

    /**
     * @return The slippage amount represented as a [BigDecimal]
     */
    private fun calculateSlippage(enteredPrice: BigDecimal): BigDecimal {
        val lastPrice = tradingPair?.lastPrice ?: return BigDecimal.ZERO
        val ratio = (enteredPrice - lastPrice) / lastPrice
        return (ratio * BigDecimal(100)).abs()
    }

}