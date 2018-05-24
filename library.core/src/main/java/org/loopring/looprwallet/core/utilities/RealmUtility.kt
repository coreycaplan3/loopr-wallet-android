package org.loopring.looprwallet.core.utilities

import io.realm.RealmConfiguration.KEY_LENGTH
import io.realm.RealmModel
import org.loopring.looprwallet.core.extensions.update
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingContainer
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
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
        kg.init(KEY_LENGTH * 8) // The key size is in bits; 8 bits in a byte --> 64 * 8
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

    inline fun <T : RealmModel> diffContainerAndAddToRepository(
            repository: BaseRealmRepository,
            oldContainer: LooprPagingContainer<T>?,
            newContainer: LooprPagingContainer<T>,
            diffPredicate: (T, T) -> Boolean
    ) {
        when {
            oldContainer != null -> {
                val pagingItem = newContainer.pagingItems.first()
                val didUpdatePagingItem = oldContainer.pagingItems.update(pagingItem) {
                    it.criteria == newContainer.criteria
                }
                if (!didUpdatePagingItem) {
                    // It's not in the list, so we can just add it
                    oldContainer.pagingItems.add(pagingItem)
                }

                newContainer.data.forEach { newData ->
                    val didUpdateOrder = oldContainer.data.update(newData) { oldData ->
                        diffPredicate(oldData, newData)
                    }
                    if (!didUpdateOrder) {
                        // It's not in the list, so we can just add it
                        oldContainer.data.add(newData)
                    }
                }

                repository.runTransaction {
                    it.upsert(oldContainer.pagingItems)
                    it.upsert(oldContainer.data)
                    it.upsert(oldContainer)
                }
            }

            else ->
                // The container doesn't exist yet
                repository.runTransaction {
                    it.upsert(newContainer.data)
                    it.upsert(newContainer)
                }
        }
    }

}