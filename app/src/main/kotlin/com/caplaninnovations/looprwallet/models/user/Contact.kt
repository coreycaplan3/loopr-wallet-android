package com.caplaninnovations.looprwallet.models.user

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class Contact(
        @PrimaryKey var address: String = "",
        var name: String = ""
) : RealmObject() {
}