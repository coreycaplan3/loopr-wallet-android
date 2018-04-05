package org.loopring.looprwallet.contacts.repositories.contacts

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.RealmResults
import io.realm.kotlin.where
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsRepository(currentWallet: LooprWallet) : BaseRealmRepository(currentWallet) {

    fun getAllContactsByName(name: String): LiveData<RealmResults<Contact>> {
        return uiSharedRealm.where<Contact>()
                .like(Contact::name, name)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getAllContactsByAddress(address: String): LiveData<RealmResults<Contact>> {
        return uiSharedRealm.where<Contact>()
                .like(Contact::address, address)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getContactByAddressNow(address: String): Contact? {
        return uiSharedRealm.where<Contact>()
                .equalTo(Contact::address, address, Case.INSENSITIVE)
                .findFirst()
    }

}