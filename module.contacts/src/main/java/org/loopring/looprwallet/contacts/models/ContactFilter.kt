package org.loopring.looprwallet.contacts.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by corey on 5/11/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@Parcelize
data class ContactFilter(val name: String?, val address: String?) : Parcelable