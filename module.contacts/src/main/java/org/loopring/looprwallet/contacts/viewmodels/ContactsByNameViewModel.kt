package org.loopring.looprwallet.contacts.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.viewmodels.OfflineOnlyViewModel
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.contacts.repositories.ContactsRepository
import org.loopring.looprwallet.core.fragments.BaseFragment

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsByNameViewModel(currentWallet: LooprWallet) : OfflineOnlyViewModel<OrderedRealmCollection<Contact>, String>() {

    override val repository = ContactsRepository(currentWallet)

    fun  getAllContactsByName(
            owner: BaseFragment,
            address: String,
            onChange: (OrderedRealmCollection<Contact>) -> Unit
    ) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<Contact>> {
        return repository.getAllContactsByName(parameter)
    }

}