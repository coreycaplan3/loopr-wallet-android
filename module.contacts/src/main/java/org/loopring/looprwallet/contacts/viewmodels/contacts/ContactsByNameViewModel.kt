package org.loopring.looprwallet.contacts.viewmodels.contacts

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.viewmodels.OfflineOnlyViewModel
import io.realm.RealmResults
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.contacts.repositories.contacts.ContactsRepository

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsByNameViewModel(currentWallet: LooprWallet) : OfflineOnlyViewModel<RealmResults<Contact>, String>(currentWallet) {

    override val repository = ContactsRepository(currentWallet)

    fun  getAllContactsByName(
            owner: LifecycleOwner,
            address: String,
            onChange: (RealmResults<Contact>) -> Unit
    ) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<RealmResults<Contact>> {
        return repository.getAllContactsByName(parameter)
    }

}