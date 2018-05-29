package org.loopring.looprwallet.tradedetails.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.os.bundleOf
import androidx.view.isVisible
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.fragment_trading_pair_details.*
import kotlinx.android.synthetic.main.trading_pair_graph.*
import kotlinx.android.synthetic.main.trading_pair_statistics_card.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createorder.activities.CreateOrderActivity
import org.loopring.looprwallet.tradedetails.R
import org.loopring.looprwallet.tradedetails.dagger.tradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.view.ChartMarkerView
import org.loopring.looprwallet.core.viewmodels.loopr.TradingPairDetailsViewModel
import org.loopring.looprwallet.tradedetails.viewmodels.TradingPairTrendViewModel
import javax.inject.Inject

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

    lateinit var filter: TradingPairGraphFilter

    @Inject
    lateinit var currencySettings: CurrencySettings

    private var tradingPair: TradingPair? = null

    private var lineData: LineData? = null

    override val layoutResource: Int
        get() = R.layout.fragment_trading_pair_details

    init {
        tradeDetailsLooprComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * We need to initialize this before the call to [launch]
         */
        tradingPairDetailsViewModel

        toolbarDelegate?.onCreateOptionsMenu = createOptionsMenu
        toolbarDelegate?.onOptionsItemSelected = optionsItemSelected

        val defaultTradingPair = TradingPairGraphFilter(primaryTicker, secondaryTicker, TradingPairGraphFilter.GRAPH_DATE_FILTER_1H)
        filter = savedInstanceState?.getParcelable(KEY_TRADING_PAIR_FILTER) ?: defaultTradingPair
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch(IO) {
            val doesTradingPairExist = tradingPairDetailsViewModel.doesTradingPairExist(filter.market, IO).await()
            if (!doesTradingPairExist) {
                launch(UI) {
                    loge("Market does not exist: ${filter.market}!", IllegalArgumentException())
                    view.context.longToast(R.string.error_trading_pair_does_not_exist)
                    activity?.finish()
                }
            }
        }

        setupOfflineFirstStateAndErrorObserver(tradingPairDetailsViewModel, tradingPairDetailsSwipeRefresh)
        tradingPairDetailsViewModel.getTradingPair(this, filter, ::bindTradingPair)

        setupChart()
        setupOfflineFirstStateAndErrorObserver(tradingPairTrendViewModel, tradingPairDetailsSwipeRefresh)
        tradingPairTrendViewModel.getTradingPairTrends(this, filter, ::bindTrendChange)
        tradingPairTrendViewModel.addCurrentStateObserver(fragmentViewLifecycleFragment!!) {
            if (tradingPairTrendViewModel.isIdle()) {
                val lineData = lineData ?: return@addCurrentStateObserver

                // We update the observer after the currentState has been changed, since the state
                // is always updated after the data is updated
                tradingPairDetailsChart.data = lineData
                tradingPairDetailsChart.legend.isEnabled = false

                if (!tradingPairDetailsChart.isVisible) {
                    tradingPairDetailsChart.isVisible = true
                }

                tradingPairDetailsChart.animateY(ApplicationUtility.int(R.integer.animation_duration))
            }
        }
    }

    override fun initializeFloatingActionButton(fab: FloatingActionButton) {
        fab.apply {
            val drawable = ApplicationUtility.drawable(R.drawable.ic_add_white_24dp, fab.context)
            DrawableCompat.setTint(drawable, Color.WHITE)
            this.setImageDrawable(drawable)
            this.setOnClickListener {
                activity?.let { CreateOrderActivity.route(it, tradingPair) }
            }
        }
    }

    private val createOptionsMenu: (Toolbar?) -> Unit = {
        it?.menu?.clear()
        it?.inflateMenu(R.menu.menu_trading_pair_details)
        it?.menu?.findItem(R.id.menuTradingPairFavorite)?.let<MenuItem, Unit> {
            when {
                tradingPair?.isFavorite == true -> it.setIcon(R.drawable.ic_favorite_white_24dp)
                else -> it.setIcon(R.drawable.ic_favorite_border_white_24dp)
            }
        }
    }

    private val optionsItemSelected: (MenuItem?) -> Boolean = { item ->
        when (item?.itemId) {
            R.id.menuTradingPairFavorite -> {
                tradingPair?.let<TradingPair, Unit> { tradingPair ->

                    val market = tradingPair.market // For passing the value across threads
                    launch(IO) {
                        looprMarketsRepository.toggleIsFavorite(market).await()

                        launch(UI) {
                            val snackbarText = when {
                                tradingPair.isFavorite -> str(R.string.formatter_is_favorite).format(tradingPair.primaryTicker)
                                else -> str(R.string.formatter_is_not_favorite).format(tradingPair.primaryTicker)
                            }
                            view?.snackbar(snackbarText)
                        }
                    }
                }
                true
            }
            R.id.menuTradingPairUnlockToken -> {
                // TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_TRADING_PAIR_FILTER, filter)
    }

    // MARK - Private Methods

    /**
     * This function takes time to execute and can block for ~350ms
     */
    private fun setupChart() = async(IO) {
        var currentButton = tradingPairDetails1hButton

        fun onButtonClick(view: View) {
            val newButton = view as Button
            if (currentButton != newButton) {
                val theme = view.context?.theme ?: return
                currentButton.setTextColor(col(theme.getResourceIdFromAttrId(R.attr.colorPrimary)))
                currentButton.setBackgroundResource(R.drawable.ripple_effect_accent)
                currentButton = newButton

                newButton.setTextColor(col(theme.getResourceIdFromAttrId(android.R.attr.textColorPrimaryInverseDisableOnly)))
                newButton.setBackgroundResource(R.drawable.button_background_material)

                val owner = this@TradingPairDetailsFragment
                tradingPairTrendViewModel.getTradingPairTrends(owner, filter, ::bindTrendChange)
            }
        }

        tradingPairDetails1hButton.setOnClickListener(::onButtonClick)
        tradingPairDetails2hButton.setOnClickListener(::onButtonClick)
        tradingPairDetails4hButton.setOnClickListener(::onButtonClick)
        tradingPairDetails1dButton.setOnClickListener(::onButtonClick)
        tradingPairDetails1wButton.setOnClickListener(::onButtonClick)

        tradingPairDetailsChart.apply {
            description.isEnabled = false

            onChartGestureListener = object : TradingPairChartGestureListener() {
                override fun onChartGestureEnd(event: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                    lockableNestedScrollView.isScrollEnabled = true
                }

                override fun onChartLongPressed(event: MotionEvent?) {
                    lockableNestedScrollView.isScrollEnabled = false
                }
            }

            marker = ChartMarkerView(this@apply.context, R.layout.custom_marker)

            setDrawGridBackground(false)

            isHighlightPerDragEnabled = true
            isHighlightPerTapEnabled = false

            isDoubleTapToZoomEnabled = false
            setPinchZoom(false)

            xAxis.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            disableScroll()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindTradingPair(tradingPair: TradingPair) {
        this.tradingPair = tradingPair

        toolbarDelegate?.resetOptionsMenu()

        toolbar?.title = tradingPair.market

        currencySettings::formatNumber.also { format ->
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
        if (trends.size == 0) {
            logi("Trends was empty...")
            return
        }

        val theme = tradingPairDetailsChart.context.theme
        val colorPrimary = col(theme.getResourceIdFromAttrId(R.attr.colorPrimary))
        val textColorPrimary = col(theme.getResourceIdFromAttrId(android.R.attr.textColorPrimary))

        val entries = trends.map { Entry(it.cycleDate.time.toFloat(), it.averagePrice.toFloat()) }
        val lineDataSet = LineDataSet(entries, tradingPair?.market).apply {
            this.setDrawCircles(false)
            this.setDrawCircleHole(false)
            this.setDrawValues(false)

            this.setDrawHorizontalHighlightIndicator(false)
            this.disableDashedLine()
            this.isHighlightEnabled = true

            this.color = colorPrimary
            this.lineWidth = dimen(R.dimen.graph_line_width)
            this.valueTextColor = textColorPrimary
            this.mode = LineDataSet.Mode.CUBIC_BEZIER
            this.highLightColor = textColorPrimary
            this.cubicIntensity = 0.05F
        }

        lineData = LineData(lineDataSet)
    }

    private abstract class TradingPairChartGestureListener : OnChartGestureListener {

        final override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}

        final override fun onChartSingleTapped(me: MotionEvent?) {}

        final override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}

        final override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}

        final override fun onChartDoubleTapped(me: MotionEvent?) {}

        final override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
    }

}