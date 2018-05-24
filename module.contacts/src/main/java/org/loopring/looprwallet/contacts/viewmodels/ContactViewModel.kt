package org.loopring.looprwallet.contacts.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.contacts.models.ContactFilter
import org.loopring.looprwallet.core.repositories.contacts.ContactsRepository
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.viewmodels.OfflineOnlyViewModel

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactViewModel : OfflineOnlyViewModel<OrderedRealmCollection<Contact>, ContactFilter>() {

    override val repository = ContactsRepository()

    fun deleteContact(contact: Contact) = runBlocking {
        val address = contact.address
        repository.removeContactByAddress(address).await()
    }

    fun getAllContactsByName(
            owner: ViewLifecycleFragment,
            name: String,
            onChange: (OrderedRealmCollection<Contact>) -> Unit
    ) {
        initializeData(owner, ContactFilter(name, null), onChange)
    }

    fun getAllContactsByAddress(
            owner: ViewLifecycleFragment,
            address: String,
            onChange: (OrderedRealmCollection<Contact>) -> Unit
    ) {
        initializeData(owner, ContactFilter(null, address), onChange)
    }

    override fun getLiveDataFromRepository(parameter: ContactFilter): LiveData<OrderedRealmCollection<Contact>> {
        return when {
            parameter.name != null -> repository.getAllContactsByName(parameter.name)
            parameter.address != null -> repository.getAllContactsByAddress(parameter.address)
            else -> throw IllegalArgumentException("Invalid state for filter, found: $parameter")
        }
    }

}