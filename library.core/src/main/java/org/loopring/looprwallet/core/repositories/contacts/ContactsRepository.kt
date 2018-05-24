package org.loopring.looprwallet.core.repositories.contacts

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
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
class ContactsRepository : BaseRealmRepository() {

    /**
     * Executes this removal the given [context]'s thread
     */
    fun removeContactByAddress(address: String, context: HandlerContext = IO) = async(context) {
        runTransaction(context) {
            it.where<Contact>()
                    .equalTo(Contact::address, address)
                    .findFirst()
                    ?.deleteFromRealm()
        }
    }

    fun getAllContactsByName(name: String, context: HandlerContext = UI): LiveData<OrderedRealmCollection<Contact>> {
        return getRealmFromContext(context)
                .where<Contact>()
                .like(Contact::name, name)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getAllContactsByAddress(address: String, context: HandlerContext = UI): LiveData<OrderedRealmCollection<Contact>> {
        return getRealmFromContext(context)
                .where<Contact>()
                .like(Contact::address, address)
                .sort(Contact::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getContactByAddressNow(address: String, context: HandlerContext = UI): Contact? {
        return getRealmFromContext(context)
                .where<Contact>()
                .equalTo(Contact::address, address, Case.INSENSITIVE)
                .findFirst()
    }

}