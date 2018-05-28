package org.loopring.looprwallet.homemarkets.adapters

import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_all_markets_empty.*
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.homemarkets.R

/**
 * Created by Corey on 4/13/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show a *ViewHolder* if the markets are unable to load (since they can't be
 * empty otherwise)
 */
class MarketsEmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    private val primaryTextColor by lazy {
        val context = itemView.context
        col(context.theme.getResourceIdFromAttrId(R.attr.textColorPrimaryOnly), context)
    }

    private val redTextColor by lazy {
        col(R.color.materialRed, itemView.context)
    }

    fun bind(isFiltering: Boolean, isFavorites: Boolean) = when {
        isFiltering -> {
            allMarketsEmptyLabel.setText(R.string.no_results_found)
            allMarketsEmptyImage.setImageResource(R.drawable.baseline_swap_horiz_black_48)
            ImageViewCompat.setImageTintList(allMarketsEmptyImage, ColorStateList.valueOf(primaryTextColor))
        }
        isFavorites -> {
            allMarketsEmptyLabel.setText(R.string.rationale_empty_favorites)
            allMarketsEmptyImage.setImageResource(R.drawable.ic_favorite_border_white_24dp)
            ImageViewCompat.setImageTintList(allMarketsEmptyImage, ColorStateList.valueOf(redTextColor))
        }
        else -> {
            allMarketsEmptyLabel.setText(R.string.no_connection_swipe_to_retry)
            allMarketsEmptyImage.setImageResource(R.drawable.baseline_cloud_off_black_48)
            ImageViewCompat.setImageTintList(allMarketsEmptyImage, ColorStateList.valueOf(primaryTextColor))
        }
    }

}