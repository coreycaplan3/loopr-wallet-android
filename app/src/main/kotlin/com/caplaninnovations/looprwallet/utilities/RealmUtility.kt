package com.caplaninnovations.looprwallet.utilities

import io.realm.Realm
import java.security.SecureRandom

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object RealmUtility {

    fun createKey(): ByteArray {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

}

fun Realm?.removeListenersAndClose() {
    if(this != null && !this.isClosed) {
        this.removeAllChangeListeners()
        this.close()
    }
}