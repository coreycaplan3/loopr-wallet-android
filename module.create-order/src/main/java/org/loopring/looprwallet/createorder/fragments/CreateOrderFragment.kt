package org.loopring.looprwallet.createorder.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.fragment_create_order.*
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenBalanceViewModel
import org.loopring.looprwallet.core.viewmodels.loopr.MarketsViewModel
import org.loopring.looprwallet.core.viewmodels.price.EthTokenPriceCheckerViewModel
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.dagger.createOrderLooprComponent
import org.loopring.looprwallet.createorder.viewmodels.CreateOrderViewModel
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by Corey on 2/6/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateOrderFragment : BaseFragment() {

    companion object {

        val TAG: String = CreateOrderFragment::class.java.simpleName

        private const val KEY_TRADING_PAIR = "TRADING_PAIR"

        fun getInstance(market: String?) = CreateOrderFragment().apply {
            arguments = bundleOf(KEY_TRADING_PAIR to market)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_order

    private lateinit var market: String

    private val marketsViewModel by lazy {
        LooprViewModelFactory.get<MarketsViewModel>(activity!!)
    }

    private val balanceViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenBalanceViewModel>(activity!!)
    }

    private val priceViewModel by lazy {
        LooprViewModelFactory.get<EthTokenPriceCheckerViewModel>(activity!!)
    }

    private val customTokenPriceViewModel by lazy {
        LooprViewModelFactory.get<CreateOrderViewModel>(activity!!)
    }

    private var tradingPair: TradingPair? = null

    private var hasSetPriceManually: Boolean = false

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createOrderLooprComponent.inject(this)

        val defaultMarket = arguments?.getString(KEY_TRADING_PAIR) ?: "LRC-WETH"
        market = savedInstanceState?.getString(KEY_TRADING_PAIR) ?: defaultMarket
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customTokenPriceViewModel.priceLiveData.observe(activity!!, Observer {
            if (it != null) {
                hasSetPriceManually = true
                setCurrentPrice()
            }
        })

        createOrderPriceLabel.setOnClickListener {
            val fragment = EnterPriceFragment()
            val tag = EnterPriceFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }

        createOrderReviewOrderButton.setOnClickListener {
            val fragment = ConfirmOrderFragment()
            val tag = ConfirmOrderFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_HORIZONTAL)
        }

        setupBalanceViewModel()
        setupPriceViewModel()
        setupMarketViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_TRADING_PAIR, market)
    }

    // MARK - Private Methods

    private var tokenBalances: OrderedRealmCollection<LooprToken>? = null

    private fun setupBalanceViewModel() {
        val address = currentWalletAddress ?: return

        balanceViewModel.getAllTokensWithBalances(this, address) {
            tokenBalances = it
            val primaryTicker = TradingPairGraphFilter.parseMarket(market).first
            val token = tokenBalances?.find { it.ticker == primaryTicker }
            val balance = token?.tokenBalances?.find { it.address == address }?.balance
            if (balance != null) {
                createOrderBalanceLabel.text = balance.formatAsToken(currencySettings, token)
            }
        }

        setupOfflineFirstStateAndErrorObserver(balanceViewModel, fragmentContainer)
    }

    private fun setupPriceViewModel() {
        priceViewModel.getTokenPrice(this, currencySettings.getCurrency()) {
            val secondaryTicker = TradingPairGraphFilter.parseMarket(market).second
            val token = tokenBalances?.find { it.ticker == secondaryTicker }
            val secondaryPrice = token?.findCurrencyPrice(currencySettings.getCurrency())?.price
                    ?: return@getTokenPrice
            val inputtedPriceInSecondary = getPriceFromLabel() ?: return@getTokenPrice

            val inputtedPrice = secondaryPrice.toBigDecimal() * inputtedPriceInSecondary

            createOrderEstimatedPriceTitleLabel.text = inputtedPrice.formatAsCurrency(currencySettings)

            val quantity = createOrderQuantityEditText.text.toString().toBigDecimalOrNull()
            if (quantity != null) {
                val totalPrice = (inputtedPrice * quantity).formatAsCurrency(currencySettings)
                createOrderEstimatedTotalPriceLabel.text = totalPrice
            }
        }

        setupOfflineFirstStateAndErrorObserver(priceViewModel, fragmentContainer)
    }

    private fun setupMarketViewModel() {
        val filter = TradingPairFilter(false, TradingPairFilter.CHANGE_PERIOD_1D)
        marketsViewModel.getMarkets(this, filter, TradingPairFilter.SORT_BY_TICKER_ASC) {
            tradingPair = it.find { it.market == market }
            setCurrentPrice()
        }

        setupOfflineFirstStateAndErrorObserver(marketsViewModel, fragmentContainer)
    }

    private fun setCurrentPrice() {
        val tradingPair = tradingPair ?: return
        val secondaryToken = tradingPair.secondaryToken

        val price = when {
            !hasSetPriceManually -> tradingPair.lastPrice
            else -> customTokenPriceViewModel.priceLiveData.value
        }

        createOrderPriceLabel.text = price?.formatAsToken(currencySettings, secondaryToken)

        val quantity = createOrderQuantityEditText.text.toString().toBigDecimalOrNull()
        if (price != null && quantity != null) {
            createOrderTotalPriceLabel.text = (price * quantity).formatAsToken(currencySettings, secondaryToken)
        }
    }

    private fun getPriceFromLabel(): BigDecimal? {
        return createOrderPriceLabel.text
                .split(Regex("\\s+"))
                .firstOrNull()
                ?.toBigDecimalOrNull()
    }

}