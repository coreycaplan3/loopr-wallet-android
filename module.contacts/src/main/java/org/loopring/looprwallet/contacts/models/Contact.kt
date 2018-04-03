package org.loopring.looprwallet.contacts.models

import io.realm.RealmObject
import io.realm.annotations.Index
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
        @PrimaryKey @Index var address: String = "",
        @Index var name: String = ""
) : RealmObject()