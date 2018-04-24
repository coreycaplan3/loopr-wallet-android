package org.loopring.looprwallet.contacts.repositories

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.like
import org.loopring.looprwallet.core.extensions.sort
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
class ContactsRepository(private val currentWallet: LooprWallet) : BaseRealmRepository() {

    fun getAllContactsByName(name: String): LiveData<RealmResults<Contact>> {
        return uiRealm.where<Contact>()
                .like(Contact::name, name)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getAllContactsByAddress(address: String): LiveData<RealmResults<Contact>> {
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

    override fun getRealm() = realmClient.getPrivateInstance(currentWallet)

}