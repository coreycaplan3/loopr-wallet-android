package org.loopring.looprwallet.core.utilities

import io.realm.RealmConfiguration.KEY_LENGTH
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import kotlin.reflect.KProperty

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
        val kg = KeyGenerator.getInstance("AES")
        kg.init(KEY_LENGTH)
        return kg.generateKey().encoded
    }

    /**
     * Formats the fields together by appending [orderedNestedFields] to [lastField].
     */
    fun formatNestedFields(orderedNestedFields: List<KProperty<*>>,
                           lastField: KProperty<*>): String {
        if (orderedNestedFields.lastOrNull() == lastField) {
            throw IllegalArgumentException("Invalid lastField! The last element of " +
                    "orderedNestedFields cannot be the same as lastField")
        }

        return orderedNestedFields.toMutableList()
                .apply { add(lastField) }
                .joinToString(".") { it.name }
    }

}