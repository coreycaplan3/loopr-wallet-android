package org.loopring.looprwallet.core.models.contact

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 4/4/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A contact is just a reusable alias for an address that allows you to more
 * easily identify the entity.
 */
open class Contact(
        @PrimaryKey var address: String = "",
        @Index var name: String = ""
): RealmObject()