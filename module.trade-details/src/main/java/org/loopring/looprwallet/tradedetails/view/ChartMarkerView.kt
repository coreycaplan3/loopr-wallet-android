package org.loopring.looprwallet.tradedetails.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import org.loopring.looprwallet.tradedetails.R


/**
 * Created by Corey on 5/4/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@SuppressLint("ViewConstructor")
class ChartMarkerView constructor(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val customMarkerTextView: TextView = findViewById(R.id.customMarkerTextView)

    /**
     * Callback every time the MarkerView is redrawn. Can be used to update the content
     * (user-interface)
     */
    override fun refreshContent(entry: Entry, highlight: Highlight) {

        customMarkerTextView.text = when (entry) {
            is CandleEntry ->  Utils.formatNumber(entry.high, 0, true)
            else -> Utils.formatNumber(entry.y, 0, true)
        }

        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        val x = (-(width / 2)).toFloat()
        val y = (-height).toFloat()
        return MPPointF(x, y)
    }

}