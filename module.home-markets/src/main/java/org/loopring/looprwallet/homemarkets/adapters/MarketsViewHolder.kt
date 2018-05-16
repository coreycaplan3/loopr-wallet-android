package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_markets.*
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ImageUtility
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.homemarkets.dagger.homeMarketsLooprComponent
import javax.inject.Inject

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsViewHolder(itemView: View, onTradingPairClick: (Int) -> Unit)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    @Inject
    lateinit var currencySettings: CurrencySettings

    override val containerView: View?
        get() = itemView

    init {
        homeMarketsLooprComponent.inject(this)

        itemView.setOnClickListener {
            onTradingPairClick(adapterPosition)
        }
    }

    fun bind(tradingPair: TradingPair) {
        val context = itemView.context
        val image = ImageUtility.getImageFromTicker(tradingPair.primaryTicker, context)
        marketsImage.setImageDrawable(image)

        marketsTokenNameLabel.text = tradingPair.primaryToken.name
        marketsTickerLabel.text = tradingPair.primaryTicker

        if (tradingPair.change24h?.startsWith("-") == true) {
            marketsPercentageChangeLabel.setTextColor(col(R.color.red_400))
        } else {
            marketsPercentageChangeLabel.setTextColor(col(R.color.green_400))
        }
        marketsPercentageChangeLabel.text = tradingPair.change24h

        val formatter = currencySettings.getCurrencyFormatter()
        marketsCurrentPriceLabel.text = formatter.format(tradingPair.lastPrice)
    }

}