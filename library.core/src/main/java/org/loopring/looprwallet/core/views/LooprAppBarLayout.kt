package org.loopring.looprwallet.core.views

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout.LayoutParams
import android.support.v7.widget.Toolbar
import android.view.View
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import kotlin.math.roundToInt

/**
 * Created by corey on 5/6/18
 *
 * Projec: loopr-android
 *
 * Purpose of Class:
 *
 */
object LooprAppBarLayout {

    val appName = str(R.string.app_name)

    /**
     * Creates the default appbar that is visible on most (if not all) pages
     */
    fun createMain(context: Context) = AppBarLayout(context).apply {
        // AppBar
        this.id = R.id.appbarLayout
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        // Toolbar
        val toolbar = Toolbar(context).apply {
            title = appName
            id = R.id.toolbar

            val height = dimen(context.theme.getResourceIdFromAttrId(android.R.attr.actionBarSize))
            layoutParams = AppBarLayout.LayoutParams(LayoutParams.MATCH_PARENT, height.roundToInt())
        }
        this.addView(toolbar)
    }


}