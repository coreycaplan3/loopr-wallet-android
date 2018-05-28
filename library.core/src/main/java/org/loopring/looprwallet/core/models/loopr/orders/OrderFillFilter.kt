package org.loopring.looprwallet.core.models.loopr.orders

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.loopring.looprwallet.core.models.loopr.filters.PagingFilter

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: For filtering order fills
 *
 */
@Parcelize
data class OrderFillFilter(val orderHash: String, override var pageNumber: Int) : Parcelable, PagingFilter {

    companion object {

        const val ITEMS_PER_PAGE = 50

    }

}