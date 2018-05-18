package org.loopring.looprwallet.core.models.loopr.orders

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: For filtering order fills
 *
 */
@Parcelize
data class OrderFillFilter(val orderHash: String, var pageNumber: Int) : Parcelable {

    companion object {

        const val ITEMS_PER_PAGE = 50

    }

}