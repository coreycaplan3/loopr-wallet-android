package com.caplaninnovations.looprwallet.fragments.contacts

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.adapters.contacts.ContactsAdapter
import com.caplaninnovations.looprwallet.extensions.indexOfFirstOrNull
import com.caplaninnovations.looprwallet.extensions.weakReference
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.viewmodels.LooprWalletViewModelFactory
import com.caplaninnovations.looprwallet.viewmodels.contacts.ContactsByAddressViewModel
import com.caplaninnovations.looprwallet.viewmodels.contacts.ContactsByNameViewModel
import io.realm.Case
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_view_contacts.*

/**
 * Created by Corey on 3/30/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class ViewContactsFragment : BaseFragment() {

    interface OnContactClickedListener {

        fun onContactSelected(contact: Contact)
    }

    companion object {
        val TAG: String = ViewContactsFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_view_contacts

    private var contactsByAddressViewModel: ContactsByAddressViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            return LooprWalletViewModelFactory.get<ContactsByAddressViewModel>(this, wallet)
                    .apply {
                        field = this
                    }
        }

    private var contactsByNameViewModel: ContactsByNameViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            return LooprWalletViewModelFactory.get<ContactsByNameViewModel>(this, wallet)
                    .apply {
                        field = this
                    }
        }

    var onContactClickedListener: OnContactClickedListener? by weakReference(null)

    private var contactList: RealmList<Contact>? = null

    private var selectedContactAddress: String? = null

    private lateinit var adapter: ContactsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        viewContactsRecyclerView.layoutManager = layoutManager
        viewContactsRecyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        adapter = ContactsAdapter(selectedContactAddress, this::onContactSelected)
        viewContactsRecyclerView.adapter = adapter
    }

    /**
     * Called when we need to filter the list by address
     * @param address The address that should be used as a filter.
     */
    fun searchContactsByAddress(address: String) {
        queryRealmForContactsByAddress(address)

        // If it's not empty, we're searching!
        adapter.isSearching = !TextUtils.isEmpty(address)
        adapter.notifyDataSetChanged()
    }

    fun resetRecyclerViewPosition() {
        if (adapter.itemCount > 0) {
            viewContactsRecyclerView?.smoothScrollToPosition(0)
        }
    }

    fun searchContactsByName(name: String) {
        adapter.isSearching = !TextUtils.isEmpty(name)
        queryRealmForContactsByName(name)
    }

    fun getContactByAddress(address: String): Contact? {
        return contactList?.where()
                ?.equalTo(Contact::address.name, address, Case.INSENSITIVE)
                ?.findFirst()
    }

    fun scrollToSelectedContact() = selectedContactAddress?.let { address ->
        val index = contactList?.indexOfFirstOrNull { it.address == address }
        index?.let { viewContactsRecyclerView?.scrollToPosition(it) }
    }

    fun onSelectedContactChanged(selectedContactAddress: String?) {
        adapter.onSelectedContactChanged(selectedContactAddress)
    }

    // MARK - Private Methods

    /**
     * Called from the [adapter] when a user selected a contact
     */
    private fun onContactSelected(contact: Contact) {
        onContactClickedListener?.onContactSelected(contact)
    }

    private fun queryRealmForContactsByName(name: String) {
        contactsByNameViewModel?.getAllContactsByName(this, name) {
            adapter.updateData(it)
        }
    }

    private fun queryRealmForContactsByAddress(address: String) {
        contactsByAddressViewModel?.getAllContactsByAddress(this, address) {
            adapter.updateData(it)
        }
    }

}