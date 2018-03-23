package com.caplaninnovations.looprwallet.viewmodels.contacts

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.user.ContactsRepository
import com.caplaninnovations.looprwallet.viewmodels.OfflineOnlyViewModel
import io.realm.RealmResults

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

    fun getAllContactsByName(
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