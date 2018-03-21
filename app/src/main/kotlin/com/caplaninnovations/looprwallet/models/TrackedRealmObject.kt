package com.caplaninnovations.looprwallet.models

import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface TrackedRealmObject {

    /**
     * The date (in UTC) time at which this realm object was last updated
     */
    var lastUpdated: Date

}