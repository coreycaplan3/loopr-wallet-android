package org.loopring.looprwallet.core.utilities

import io.realm.RealmConfiguration.KEY_LENGTH
import io.realm.RealmModel
import org.loopring.looprwallet.core.extensions.insertOrUpdate
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.filters.PagingFilter
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingContainer
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
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

    fun loadMorePaging(pager: LooprAdapterPager<*>, filter: PagingFilter, viewModel: OfflineFirstViewModel<*, *>) {
        pager.currentPage.let {
            filter.pageNumber = it + 1
            viewModel.refresh()
        }
    }

    fun <T : RealmModel> updatePagingContainer(
            repository: BaseRealmRepository,
            pageNumber: Int,
            oldContainer: LooprPagingContainer<T>?,
            newContainer: LooprPagingContainer<T>
    ) {
        when {
            oldContainer != null -> {
                oldContainer.pagingItem = newContainer.pagingItem

                if (pageNumber == 1) {
                    oldContainer.requirePagingItem.pageIndex = 1
                }

                repository.runTransaction(IO) {
                    it.upsert(newContainer.data)
                    it.upsert(oldContainer.requirePagingItem)
                    it.upsert(oldContainer)
                }
            }
            else -> {
                repository.runTransaction(IO) {
                    it.upsert(newContainer.data)
                    it.upsert(newContainer.requirePagingItem)
                    it.upsert(newContainer)
                }
            }
        }
    }

}