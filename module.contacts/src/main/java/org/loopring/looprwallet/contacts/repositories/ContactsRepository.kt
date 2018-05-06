package org.loopring.looprwallet.contacts.repositories

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.like
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsRepository : BaseRealmRepository(true) {

    /**
     * Executes this removal as a coroutine on the IO thread
     */
    fun removeContactByAddress(address: String) = async(IO) {
        runTransaction(Realm.Transaction {
            it.where<Contact>()
                    .equalTo(Contact::address, address)
                    .findFirst()
                    ?.deleteFromRealm()
        })
    }

    fun getAllContactsByName(name: String): LiveData<OrderedRealmCollection<Contact>> {
        return uiRealm.where<Contact>()
                .like(Contact::name, name)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getAllContactsByAddress(address: String): LiveData<OrderedRealmCollection<Contact>> {
        return uiRealm.where<Contact>()
                .like(Contact::address, address)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getContactByAddressNow(address: String): Contact? {
        return uiRealm.where<Contact>()
                .equalTo(Contact::address, address, Case.INSENSITIVE)
                .findFirst()
    }

}