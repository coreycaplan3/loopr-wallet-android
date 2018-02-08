package com.caplaninnovations.looprwallet.utilities

import io.realm.Realm
import java.security.SecureRandom

/**
 * Created by Corey Caplan on 1/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */
object RealmUtility {

    /**
     * Creates an encryption key for the database, using a 64-byte long array. It is generated
     * using the Java [SecureRandom] class.
     *
     * @return A 64-byte long array, to be used as an encryption key for the Realm database.
     */
    fun createKey(): ByteArray {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    /**
     * An optional function, used to remove listeners on a realm and close it, if it's not null
     */
    fun removeListenersAndClose(realm: Realm?) {
        if (realm != null && !realm.isClosed) {
            realm.removeAllChangeListeners()
            realm.close()
        }
    }

}