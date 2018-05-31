package org.loopring.looprwallet.createorder.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager.OnBackStackChangedListener
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_confirm_order.*
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.settings.LoopringFeeSettingsFragment
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings
import org.loopring.looprwallet.core.utilities.ImageUtility
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.dagger.createOrderLooprComponent
import org.loopring.looprwallet.createorder.viewmodels.CreateOrderViewModel
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by corey on 5/30/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ConfirmOrderFragment : BaseFragment(), OnBackStackChangedListener {

    companion object {

        val TAG: String = ConfirmOrderFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_confirm_order

    private val createOrderViewModel by lazy {
        LooprViewModelFactory.get<CreateOrderViewModel>(activity!!)
    }

    @Inject
    lateinit var currencySettings: CurrencySettings

    @Inject
    lateinit var loopringFeeSettings: LoopringFeeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createOrderLooprComponent.inject(this)

        toolbarDelegate?.onCreateOptionsMenu = ::createOptionsMenu
        toolbarDelegate?.onOptionsItemSelected = ::optionsMenuItemSelected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createOrderViewModel.order.observe(fragmentViewLifecycleFragment!!, Observer {
            if (it != null) {
                bindMiscInformation(it, view.context)
                bindPriceInformation(it)
            }
        })

        // Used to rebind the fee settings after they changed in the LoopringFeeSettingsFragment
        requireFragmentManager().addOnBackStackChangedListener(this)

        confirmOrderPlaceOrderButton.setOnClickListener {
            // TODO place order
            logd("Placing order…")
        }

    }

    override fun onBackStackChanged() {
        bindFeeSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        requireFragmentManager().removeOnBackStackChangedListener(this)
    }

    // MARK - Private Methods

    private fun createOptionsMenu(toolbar: Toolbar?) {
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.menu_confirm_order)
    }

    private fun optionsMenuItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menuConfirmOrderFeeSettings -> {
            val fragment = LoopringFeeSettingsFragment()
            val tag = LoopringFeeSettingsFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
            true
        }
        else -> false
    }

    private fun bindMiscInformation(order: AppLooprOrder, context: Context) {
        val originLabelText: String
        val originToken: LooprToken
        val originTokenAmount: BigDecimal

        val destinationToken: LooprToken
        val destinationTokenAmount: BigDecimal
        when {
            order.isSell -> {
                // LRC-WETH --> sell LRC for WETH; Origin = LRC, Destination = WETH
                originToken = order.tradingPair.primaryToken
                originTokenAmount = order.amount.toBigDecimal(originToken)
                originLabelText = getString(R.string.you_sell)

                destinationToken = order.tradingPair.secondaryToken
                destinationTokenAmount = order.amount.toBigDecimal(originToken) * order.priceInSecondaryTicker
            }
            else -> {
                // LRC-WETH --> Buy LRC with WETH; Origin = WETH, Destination = LRC
                destinationToken = order.tradingPair.primaryToken
                destinationTokenAmount = order.amount.toBigDecimal(destinationToken)

                originToken = order.tradingPair.secondaryToken
                originTokenAmount = order.amount.toBigDecimal(destinationToken) * order.priceInSecondaryTicker
                originLabelText = getString(R.string.you_buy)
            }
        }

        confirmOrderOriginLabel.text = originLabelText
        confirmOrderOriginTokenImage.setImageDrawable(ImageUtility.getImageFromTicker(originToken.ticker, context))
        confirmOrderOriginTokenQuantityLabel.text = originTokenAmount.formatAsToken(currencySettings, originToken)

        confirmOrderDestinationImage.setImageDrawable(ImageUtility.getImageFromTicker(destinationToken.ticker, context))
        confirmOrderDestinationTokenQuantityLabel.text = destinationTokenAmount.formatAsToken(currencySettings, destinationToken)
    }

    @SuppressLint("SetTextI18n")
    private fun bindPriceInformation(order: AppLooprOrder) {
        val price = order.priceInSecondaryTicker.formatAsTokenNoTicker(currencySettings)
        val primaryTicker = order.tradingPair.primaryTicker
        val secondaryTicker = order.tradingPair.secondaryTicker

        confirmOrderPriceLabel.text = "$price $primaryTicker / $secondaryTicker"

        val wethToken = createOrderViewModel.lrcWethTradingPair.value?.secondaryToken ?: return
        val wethFiatPrice = wethToken.findCurrencyPrice(currencySettings.getCurrency())?.toBigDecimal()
                ?: return
        val fiatPrice = order.priceInSecondaryTicker * wethFiatPrice
        confirmOrderEstimatedPriceFiatLabel.text = "~ ${fiatPrice.formatAsCurrency(currencySettings)}"

        val totalFiatPrice = wethFiatPrice * order.totalPrice
        confirmOrderOriginFiatLabel.text = "~ ${totalFiatPrice.formatAsCurrency(currencySettings)}"
        confirmOrderDestinationFiatLabel.text = "~ ${totalFiatPrice.formatAsCurrency(currencySettings)}"
    }

    private fun bindFeeSettings() {
        confirmOrderMarginSplitLabel.text = loopringFeeSettings.currentMarginSplit.formatAsPercentage(currencySettings)

        val feePercentage = loopringFeeSettings.currentLrcFeePercentage
        val lrcToken = createOrderViewModel.lrcWethTradingPair.value ?: return
        val lrcPriceInWeth = lrcToken.lastPrice
        val totalOrderPrice = createOrderViewModel.order.value?.totalPrice ?: return
        val lrcFee = (totalOrderPrice * feePercentage) / lrcPriceInWeth
        confirmOrderLrcFeeLabel.text = lrcFee.formatAsToken(currencySettings, lrcToken.primaryToken)

        val nativeCurrencyPrice = lrcToken.primaryToken.findCurrencyPrice(currencySettings.getCurrency())
                ?: return
        confirmOrderEstimatedLrcFeeFiatLabel.text = (nativeCurrencyPrice.toBigDecimal() * lrcFee).formatAsCurrency(currencySettings)
    }

}