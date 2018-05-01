package org.loopring.looprwallet.contacts.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.fragment_view_contacts.*
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.contacts.adapters.ContactsAdapter
import org.loopring.looprwallet.contacts.viewmodels.ContactsByAddressViewModel
import org.loopring.looprwallet.contacts.viewmodels.ContactsByNameViewModel
import org.loopring.looprwallet.core.extensions.indexOfFirstOrNull
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory

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

    private val contactsByAddressViewModel by lazy {
        LooprViewModelFactory.get<ContactsByAddressViewModel>(this)
    }

    private val contactsByNameViewModel by lazy {
        LooprViewModelFactory.get<ContactsByNameViewModel>(this)
    }

    var selectedContactAddress: String? = null

    var onContactClickedListener: OnContactClickedListener? by weakReference(null)

    private var adapter: ContactsAdapter? = null

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
        adapter?.isSearching = !TextUtils.isEmpty(address)
        adapter?.notifyDataSetChanged()
    }

    fun resetRecyclerViewPosition() {
        val adapter = adapter ?: return
        if (adapter.itemCount > 0) {
            viewContactsRecyclerView?.smoothScrollToPosition(0)
        }
    }

    fun searchContactsByName(name: String) {
        adapter?.isSearching = !TextUtils.isEmpty(name)
        queryRealmForContactsByName(name)
    }

    /**
     * Scrolls to the selected contact, if a contact is selected
     *
     * @param address The address of the selected contact or null if there isn't one.
     */
    fun scrollToSelectedContact(address: String?) {
        address ?: return

        adapter?.data
                ?.indexOfFirstOrNull { it.address == address }
                ?.let { viewContactsRecyclerView?.scrollToPosition(it) }
    }

    /**
     * Called when the contact that is selected in the RecyclerView changes.
     *
     * @param address The address of the selected contact or null if there isn't a selected contact.
     */
    fun onSelectedContactChanged(address: String?) {
        adapter?.onSelectedContactChanged(address)
    }

    // MARK - Private Methods

    /**
     * Called from the [adapter] when a user selected a contact
     */
    private fun onContactSelected(contact: Contact) {
        onContactClickedListener?.onContactSelected(contact)
    }

    private fun queryRealmForContactsByName(name: String) {
        contactsByNameViewModel.getAllContactsByName(this, name) {
            adapter?.updateData(it)
        }
    }

    private fun queryRealmForContactsByAddress(address: String) {
        contactsByAddressViewModel.getAllContactsByAddress(this, address) {
            adapter?.updateData(it)
        }
    }

}