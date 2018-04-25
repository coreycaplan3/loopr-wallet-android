package org.loopring.looprwallet.tradedetails.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.os.bundleOf
import androidx.view.isVisible
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.fragment_trading_pair_details.*
import kotlinx.android.synthetic.main.trading_pair_graph.*
import kotlinx.android.synthetic.main.trading_pair_statistics_card.*
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.int
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createorder.activities.CreateOrderActivity
import org.loopring.looprwallet.tradedetails.R
import org.loopring.looprwallet.tradedetails.dagger.tradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.viewmodels.TradingPairDetailsViewModel
import org.loopring.looprwallet.tradedetails.viewmodels.TradingPairTrendViewModel

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairDetailsFragment : BaseFragment() {

    companion object {

        val TAG: String = TradingPairDetailsFragment::class.java.simpleName

        private const val KEY_PRIMARY_TICKER = "_PRIMARY_TICKER"
        private const val KEY_SECONDARY_TICKER = "_SECONDARY_TICKER"

        private const val KEY_TRADING_PAIR_FILTER = "_TRADING_PAIR_FILTER"

        fun getInstance(tradingPair: TradingPair) = TradingPairDetailsFragment().apply {
            arguments = bundleOf(
                    KEY_PRIMARY_TICKER to tradingPair.primaryTicker,
                    KEY_SECONDARY_TICKER to tradingPair.secondaryTicker
            )
        }

    }

    private val primaryTicker: String by lazy {
        arguments?.getString(KEY_PRIMARY_TICKER)!!
    }

    private val secondaryTicker: String by lazy {
        arguments?.getString(KEY_SECONDARY_TICKER)!!
    }

    private val tradingPairDetailsViewModel by lazy {
        LooprViewModelFactory.get<TradingPairDetailsViewModel>(this)
    }

    private val tradingPairTrendViewModel by lazy {
        LooprViewModelFactory.get<TradingPairTrendViewModel>(this)
    }

    private val looprMarketsRepository by lazy {
        LooprMarketsRepository()
    }

    lateinit var filter: TradingPairFilter

    lateinit var currencySettings: CurrencySettings

    private var tradingPair: TradingPair? = null

    override val layoutResource: Int
        get() = R.layout.fragment_trading_pair_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tradeDetailsLooprComponent.inject(this)

        val defaultTradingPair = TradingPairFilter(primaryTicker, secondaryTicker, TradingPairFilter.GRAPH_DATE_FILTER_1H)
        filter = savedInstanceState?.getParcelable(KEY_TRADING_PAIR_FILTER) ?: defaultTradingPair
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfflineFirstStateAndErrorObserver(tradingPairDetailsViewModel, tradingPairDetailsSwipeRefresh)
        tradingPairDetailsViewModel.getTradingPair(this, filter, ::bindTradingPair)

        setupOfflineFirstStateAndErrorObserver(tradingPairTrendViewModel, tradingPairDetailsSwipeRefresh)
        tradingPairTrendViewModel.getTradingPairTrends(this, filter, ::bindTrendChange)
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp)
        floatingActionButton.setOnClickListener {
            activity?.let { CreateOrderActivity.route(it, tradingPair) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_trading_pair_details, menu)

        menu?.findItem(R.id.menuTradingPairFavorite)?.ifNotNull {
            if (tradingPair?.isFavorite == true) {
                it.setIcon(R.drawable.ic_favorite_white_24dp)
            } else {
                it.setIcon(R.drawable.ic_favorite_border_white_24dp)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.menuTradingPairFavorite -> {
            tradingPair?.ifNotNull {
                looprMarketsRepository.toggleIsFavorite(it)

                val message = when {
                    it.isFavorite -> str(R.string.formatter_is_favorite).format(it.primaryTicker)
                    else -> str(R.string.formatter_is_not_favorite).format(it.primaryTicker)
                }

                activity?.invalidateOptionsMenu()
                view?.snackbar(message)
            }
            true
        }
        R.id.menuTradingPairUnlockToken -> {
            // TODO
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_TRADING_PAIR_FILTER, filter)
    }

    // MARK - Private Methods

    @SuppressLint("SetTextI18n")
    private fun bindTradingPair(tradingPair: TradingPair) {
        this.tradingPair = tradingPair

        // TODO check if these numbers are formatted as native currency or tokens
        currencySettings.getCurrencyFormatter()

        currencySettings.getNumberFormatter().apply {
            val secondaryTicker = tradingPair.secondaryTicker

            tradingPairStatsCurrentLabel.text = "${format(tradingPair.lastPrice)} $secondaryTicker"
            tradingPairStatsHighLabel.text = "${format(tradingPair.highPrice)} $secondaryTicker"
            tradingPairStatsLowLabel.text = "${format(tradingPair.lowPrice)} $secondaryTicker"
            tradingPairStatsVolumeLabel.text = "${format(tradingPair.volumeOfSecondary)} $secondaryTicker"
        }

        tradingPairStats24hChangeLabel.text = tradingPair.change24h
        if (tradingPair.change24h.startsWith("-")) {
            tradingPairStats24hChangeLabel.setTextColor(col(R.color.red_400))
        } else {
            tradingPairStats24hChangeLabel.setTextColor(col(R.color.green_400))
        }
    }

    private fun bindTrendChange(trends: OrderedRealmCollection<TradingPairTrend>) {
        if (tradingPairDetailsChartProgress.isVisible) {
            tradingPairDetailsChartProgress.visibility = View.GONE
            tradingPairDetailsChart.visibility = View.VISIBLE
        }

        val entries = trends.map { Entry(it.cycleDate.time.toFloat(), it.averagePrice.toFloat()) }
        val lineDataSet = LineDataSet(entries, "").apply {
            this.disableDashedLine()
            this.setDrawCircles(false)
            this.setDrawCircleHole(false)
            this.mode = LineDataSet.Mode.LINEAR
            this.isHighlightEnabled = true
            this.cubicIntensity = 0.05F
            this.form = null

            context?.theme?.ifNotNull {
                this.color = col(it.getResourceIdFromAttrId(R.attr.colorPrimary))
                this.valueTextColor = col(it.getResourceIdFromAttrId(android.R.attr.textColorPrimary))
            }
        }

        val lineData = LineData(lineDataSet)

        tradingPairDetailsChart.data = lineData

        if (tradingPairDetailsChart.isVisible) {
            tradingPairDetailsChart.invalidate()
        } else {
            // It's not visible so we'll show it and animate it
            tradingPairDetailsChart.visibility = View.VISIBLE
            tradingPairDetailsChart.animateX(int(R.integer.animation_duration))
        }
    }

}