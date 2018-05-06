package org.loopring.looprwallet.contacts.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.contacts.repositories.ContactsRepository
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
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
class ContactsByNameViewModel : OfflineOnlyViewModel<OrderedRealmCollection<Contact>, String>() {

    override val repository = ContactsRepository()

    fun deleteContact(contact: Contact) = runBlocking<Unit> {
        val address = contact.address
        repository.removeContactByAddress(address).await()
    }

    fun getAllContactsByName(
            owner: ViewLifecycleFragment,
            name: String,
            onChange: (OrderedRealmCollection<Contact>) -> Unit
    ) {
        initializeData(owner, name, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<Contact>> {
        return repository.getAllContactsByName(parameter)
    }

}