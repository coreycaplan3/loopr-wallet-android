package com.caplaninnovations.looprwallet.repositories.user

import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.extensions.asLiveData
import com.caplaninnovations.looprwallet.extensions.equalTo
import com.caplaninnovations.looprwallet.extensions.like
import com.caplaninnovations.looprwallet.extensions.sort
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import io.realm.Case
import io.realm.RealmResults
import io.realm.kotlin.where

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