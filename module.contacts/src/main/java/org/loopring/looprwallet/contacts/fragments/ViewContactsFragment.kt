package org.loopring.looprwallet.contacts.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import androidx.os.bundleOf
import androidx.view.isVisible
import kotlinx.android.synthetic.main.fragment_view_contacts.*
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.contacts.adapters.ContactsAdapter
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.contacts.viewmodels.ContactViewModel
import org.loopring.looprwallet.core.adapters.LooprLayoutManager
import org.loopring.looprwallet.core.extensions.indexOfFirstOrNull
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
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

        private const val KEY_TYPE = "TYPE"
        private const val KEY_SEARCHABLE = "SEARCHABLE"
        private const val KEY_VIEW_ALL = "VIEW_ALL"

        fun getSearchableInstance() = ViewContactsFragment().apply {
            arguments = bundleOf(KEY_TYPE to KEY_SEARCHABLE)
        }

        fun getViewAllInstance() = ViewContactsFragment().apply {
            arguments = bundleOf(KEY_TYPE to KEY_VIEW_ALL)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_view_contacts

    private val contactViewModel by lazy {
        LooprViewModelFactory.get<ContactViewModel>(this)
    }

    var selectedContactAddress: String? = null

    var onContactClickedListener: OnContactClickedListener? = null

    private val fragmentType by lazy {
        arguments?.getString(KEY_TYPE)!!
    }

    private var adapter: ContactsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewContactsRecyclerView.layoutManager = LooprLayoutManager(context)

        toolbar?.title = getString(R.string.my_contact)

        adapter = ContactsAdapter(selectedContactAddress, this::onContactSelected)
        viewContactsRecyclerView.adapter = adapter

        contactViewModel.getAllContactsByName(this, "*") {
            adapter?.updateData(it)
        }

        when (fragmentType) {
            KEY_VIEW_ALL -> {
                onContactClickedListener = object : OnContactClickedListener {
                    override fun onContactSelected(contact: Contact) {
                        showDeleteContactDialog(view, contact)
                    }
                }
            }
        }

    }

    override fun initializeFloatingActionButton(fab: FloatingActionButton) {

        when (fragmentType) {
            KEY_VIEW_ALL -> fab.apply {
                isVisible = true

                val fabDrawable = ApplicationUtility.drawable(R.drawable.ic_person_add_white_24dp, fab.context)
                DrawableCompat.setTint(fabDrawable, Color.WHITE)
                setImageDrawable(fabDrawable)

                setOnClickListener {
                    CreateContactDialog.getInstance(null)
                            .show(fragmentManager, CreateContactDialog.TAG)
                }
            }
        }

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
        if (fragmentType == KEY_VIEW_ALL) {
            // We want to reset it, since the ViewAll variant doesn't perform highlighting (whereas
            // the Search variant does)
            adapter?.selectedContactAddress = null
        }
    }

    private fun queryRealmForContactsByName(name: String) {
        contactViewModel.getAllContactsByName(this, name) {
            adapter?.updateData(it)
        }
    }

    private fun queryRealmForContactsByAddress(address: String) {
        val addressToSearch = when {
            address.trim().isEmpty() -> "*"
            else -> "$address*"
        }

        contactViewModel.getAllContactsByAddress(this, addressToSearch) {
            adapter?.updateData(it)
        }
    }

    private fun showDeleteContactDialog(view: View, contact: Contact) {
        val message = str(R.string.formatter_delete_contact).format(contact.name)
        AlertDialog.Builder(view.context)
                .setTitle(R.string.delete_contact)
                .setMessage(message)
                .setPositiveButton(R.string.delete) { dialog, _ ->
                    contactViewModel.deleteContact(contact)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

}