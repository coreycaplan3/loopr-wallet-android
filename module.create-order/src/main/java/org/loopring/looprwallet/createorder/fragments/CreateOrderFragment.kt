package org.loopring.looprwallet.createorder.fragments

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.os.bundleOf
import androidx.view.isVisible
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.fragment_create_order.*
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.presenters.TokenNumberPadPresenter
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenBalanceViewModel
import org.loopring.looprwallet.core.viewmodels.loopr.MarketsViewModel
import org.loopring.looprwallet.core.viewmodels.price.LooprTokenPriceCheckerViewModel
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
        private const val KEY_IS_SELL = "IS_SELL"

        fun getInstance(isSell: Boolean, market: String?) = CreateOrderFragment().apply {
            arguments = bundleOf(
                    KEY_IS_SELL to isSell,
                    KEY_TRADING_PAIR to market
            )
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
        LooprViewModelFactory.get<LooprTokenPriceCheckerViewModel>(activity!!)
    }

    private val createOrderViewModel by lazy {
        LooprViewModelFactory.get<CreateOrderViewModel>(activity!!)
    }

    private var tradingPair: TradingPair?
        get() = createOrderViewModel.tradingPair.value
        set(value) {
            if (value == null || value == createOrderViewModel.tradingPair.value) {
                return
            }

            createOrderViewModel.order.value?.tradingPair = value
            createOrderViewModel.tradingPair.value = value
        }

    private val isPriceSetManually: Boolean
        get() = createOrderViewModel.priceLiveData.value != null

    private var isSell: Boolean
        get() = createOrderViewModel.order.value?.isSell == true
        set(value) {
            createOrderViewModel.order.value?.isSell = value
        }

    private lateinit var tokenNumberPadPresenter: TokenNumberPadPresenter

    private val appbarBackground: TransitionDrawable by lazy {
        drawable(R.drawable.buy_sell_background, context!!) as TransitionDrawable
    }

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createOrderLooprComponent.inject(this)

        val defaultMarket = arguments?.getString(KEY_TRADING_PAIR) ?: "LRC-WETH"
        market = savedInstanceState?.getString(KEY_TRADING_PAIR) ?: defaultMarket

        val isSell = arguments?.getBoolean(KEY_IS_SELL, false)!!
        createOrderViewModel.order.value!!.isSell = isSell

        toolbarDelegate?.onCreateOptionsMenu = ::createOptionsMenu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenNumberPadPresenter = TokenNumberPadPresenter(this, createOrderQuantityEditText, ::onFormChanged)

        appbarLayout?.background = appbarBackground

        if (isSell) {
            appbarBackground.startTransition(300)
            toolbar?.setTitle(R.string.limit_sell)
        } else {
            toolbar?.setTitle(R.string.limit_buy)
        }

        if (tradingPair == null) {
            createOrderProgressContainer.isVisible = true
            createOrderContainer.isVisible = false
            setupTradingPair()
        }

        createOrderViewModel.order.value?.isSell = isSell

        createOrderViewModel.priceLiveData.observe(activity!!, Observer {
            if (it != null) {
                bindBalanceInformation()
                bindPriceInformation()
            }
        })

        createOrderPriceLabel.setOnClickListener {
            val fragment = EnterPriceFragment()
            val tag = EnterPriceFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }

        createOrderMaxButton.setOnClickListener {
            // TODO check gas fee, LRC frozen, LRC
        }

        createOrderReviewButton.setOnClickListener {
            val fragment = ConfirmOrderFragment()
            val tag = ConfirmOrderFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_HORIZONTAL)
        }

        setupBalanceViewModel()
        setupPriceViewModel()
        setupMarketViewModel()
    }

    override fun createAppbarLayout(fragmentView: ViewGroup): AppBarLayout {
        val appbar = super.createAppbarLayout(fragmentView)
        val tabLayout = TabLayout(fragmentView.context).apply {
            this.setSelectedTabIndicatorColor(Color.WHITE)
            this.addTab(this.newTab().setText(R.string.buy), !isSell)
            this.addTab(this.newTab().setText(R.string.sell), isSell)
            this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == 0) {
                        isSell = false
                        toolbar?.setTitle(R.string.limit_buy)
                        appbarBackground.reverseTransition(300)
                    } else {
                        isSell = true
                        appbarBackground.startTransition(300)
                        toolbar?.setTitle(R.string.limit_sell)
                    }
                }
            })
        }
        appbar.addView(tabLayout)
        return appbar
    }

    override fun onFormChanged() {
        val quantity = createOrderQuantityEditText.text.toString().toBigDecimalOrNull()
        if (quantity == null || quantity == BigDecimal.ZERO) {
            createOrderReviewButton.isEnabled = false
            return
        }

        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        val dealtToken = createOrderViewModel.order.value?.dealtToken

        val balance = dealtToken?.findAddressBalance(address)?.toBigDecimal()

        val isBalanceSufficient = quantity <= balance
        createOrderReviewButton.isEnabled = isBalanceSufficient
        if (!isBalanceSufficient) {
            context?.longToast(R.string.formatter_error_insufficient_balance)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_TRADING_PAIR, market)
    }

    // MARK - Private Methods

    private fun createOptionsMenu(toolbar: Toolbar?) {
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.menu_create_order)

        val spinner = toolbar?.menu?.findItem(R.id.menuCreateOrderSelectTradingPair)?.actionView as? Spinner
                ?: return
        createOrderViewModel.allTradingPairs.observe(fragmentViewLifecycleFragment!!, Observer {
            if (it == null) {
                // Guard
                return@Observer
            }

            val snapshot = it.createSnapshot()
            val list = snapshot.map { it.market }
            val adapter = ArrayAdapter<String>(spinner.context, android.R.layout.simple_spinner_item, list)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (tradingPair?.market == snapshot[position]?.market) {
                        // The user didn't select a new trading pair
                        return
                    }

                    tradingPair = snapshot[position]
                    resetViewState()
                    bindBalanceInformation()
                    bindPriceInformation()
                }
            }
        })
    }

    private fun setupTradingPair() = createOrderViewModel.allTradingPairs.observe(fragmentViewLifecycleFragment!!, Observer {
        if (it == null) {
            return@Observer
        }

        val searchElement = TradingPair(market)
        val snapshot = it.createSnapshot()
        val index = snapshot.binarySearch(searchElement, Comparator { x1, x2 ->
            x1.market.compareTo(x2.market)
        })
        if (index >= 0) {
            createOrderProgressContainer.isVisible = false
            createOrderContainer.isVisible = true
            tradingPair = snapshot[index]
        }
    })

    private var tokenBalances: OrderedRealmCollection<LooprToken>? = null

    private fun setupBalanceViewModel() {
        val address = currentWalletAddress ?: return

        balanceViewModel.getAllTokensWithBalances(this, address) {
            tokenBalances = it
            bindBalanceInformation()
        }

        setupOfflineFirstStateAndErrorObserver(balanceViewModel, fragmentContainer)
    }

    private fun setupPriceViewModel() {
        priceViewModel.getTokenPrice(this, currencySettings.getCurrency()) {
            bindPriceInformation()
            bindBalanceInformation()
        }

        setupOfflineFirstStateAndErrorObserver(priceViewModel, fragmentContainer)
    }

    private fun setupMarketViewModel() {
        val filter = TradingPairFilter(false, TradingPairFilter.CHANGE_PERIOD_1D)
        marketsViewModel.getMarkets(this, filter, TradingPairFilter.SORT_BY_TICKER_ASC) {
            tradingPair = it.find { it.market == market }
            bindPriceInformation()
            bindBalanceInformation()
        }

        setupOfflineFirstStateAndErrorObserver(marketsViewModel, fragmentContainer)
    }

    /**
     * Called after a new trading pair is selected and the state needs to be reset
     */
    private fun resetViewState() {
        createOrderViewModel.priceLiveData.value = null
        createOrderQuantityEditText.text = null
    }

    private fun bindPriceInformation() {

        val tradingPair = tradingPair ?: return
        val currency = currencySettings.getCurrency()

        fun setCurrentPrice() {
            val price = when {
                !isPriceSetManually -> tradingPair.lastPrice
                else -> createOrderViewModel.priceLiveData.value
            }

            val secondaryToken = tradingPair.secondaryToken

            createOrderPriceLabel.text = price?.formatAsToken(currencySettings, secondaryToken)

            val quantity = createOrderQuantityEditText.text.toString().toBigDecimalOrNull()
            if (price != null && quantity != null) {
                createOrderTotalPriceLabel.text = (price * quantity).formatAsToken(currencySettings, secondaryToken)
            } else {
                createOrderTotalPriceLabel.text = BigDecimal.ZERO.formatAsToken(currencySettings, secondaryToken)
            }
        }

        fun setTotalPrice(inputtedPrice: BigDecimal) {
            val quantity = createOrderQuantityEditText.text.toString().toBigDecimalOrNull()
            if (quantity != null) {
                val totalPrice = (inputtedPrice * quantity).formatAsCurrency(currencySettings)
                createOrderEstimatedTotalPriceLabel.text = totalPrice
            } else {
                createOrderEstimatedTotalPriceLabel.text = BigDecimal.ZERO.formatAsCurrency(currencySettings)
            }
        }

        val token = tokenBalances?.find { it.ticker == tradingPair.secondaryTicker }
        val secondaryPrice = token?.findCurrencyPrice(currency)?.toBigDecimal()
        val inputtedPriceInSecondary = getPriceFromLabel()

        val inputtedPrice: BigDecimal
        if (secondaryPrice != null && inputtedPriceInSecondary != null) {
            inputtedPrice = secondaryPrice * inputtedPriceInSecondary
            createOrderEstimatedPriceFiatTitleLabel.text = inputtedPrice.formatAsCurrency(currencySettings)
        } else {
            inputtedPrice = BigDecimal.ZERO
        }

        setTotalPrice(inputtedPrice)
        setCurrentPrice()

    }

    private fun bindBalanceInformation() {
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        val tradingPair = tradingPair ?: return

        val token = tokenBalances?.find { it.ticker == tradingPair.primaryTicker }
        val balance = token?.tokenBalances?.find { it.address == address }?.balance
        if (balance != null) {
            createOrderBalanceLabel.text = balance.formatAsToken(currencySettings, token)
        }
    }

    private fun getPriceFromLabel(): BigDecimal? {
        return createOrderPriceLabel.text
                .split(Regex("\\s+"))
                .firstOrNull()
                ?.toBigDecimalOrNull()
    }

}