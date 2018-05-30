package org.loopring.looprwallet.createorder.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_confirm_order.*
import org.loopring.looprwallet.core.extensions.formatAsPercentage
import org.loopring.looprwallet.core.extensions.formatAsTokenNoTicker
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.dagger.createOrderLooprComponent
import org.loopring.looprwallet.createorder.viewmodels.CreateOrderViewModel
import javax.inject.Inject

/**
 * Created by corey on 5/30/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ConfirmOrderFragment : BaseFragment() {

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createOrderViewModel.order.observe(fragmentViewLifecycleFragment!!, Observer {
            if (it != null) {
                bindOrderInformation(it)
            }
        })

        confirmOrderPlaceOrderButton.setOnClickListener {
            // TODO place order
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindOrderInformation(order: AppLooprOrder) {
        val price = order.priceInSecondaryTicker.formatAsTokenNoTicker(currencySettings)
        val primaryTicker = order.tradingPair.primaryTicker
        val secondaryTicker = order.tradingPair.secondaryTicker

        confirmOrderPriceLabel.text = "$price $primaryTicker / $secondaryTicker"


    }

    private fun bindFeeSettings() {
        val lrcPrice = createOrderViewModel.lrcWethTradingPair.value?.lastPrice ?: return
        confirmOrderLrcFeeLabel.text = "" // TODO - default to 0.002 of the trade price in WETH to LRC
        confirmOrderMarginSplitLabel.text = loopringFeeSettings.currentMarginSplit.formatAsPercentage(currencySettings)
    }

}