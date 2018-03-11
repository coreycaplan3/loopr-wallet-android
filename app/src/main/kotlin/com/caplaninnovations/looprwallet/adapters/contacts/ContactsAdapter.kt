package com.caplaninnovations.looprwallet.adapters.contacts

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.utilities.indexOfFirstOrNull
import com.caplaninnovations.looprwallet.utilities.inflate

/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsAdapter(
        var contactList: List<Contact>,
        private var selectedContactAddress: String?,
        private var onContactSelected: (Contact) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_CONTACT = 1
        private const val TYPE_NO_CONTACTS_FOUND = 2
    }

    var isSearching = false

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY -> EmptyContactsViewHolder(parent.inflate(R.layout.view_holder_contact_empty))
            TYPE_NO_CONTACTS_FOUND -> NoContactsFoundViewHolder(parent.inflate(R.layout.view_holder_no_contact_found))
            TYPE_CONTACT -> ContactsViewHolder(parent.inflate(R.layout.view_holder_contact))
            else -> throw IllegalArgumentException("Invalid type, found: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return if (contactList.isEmpty()) 1 else contactList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ContactsViewHolder)?.bind(contactList[position], selectedContactAddress, this::onContactViewHolderSelected)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && contactList.isEmpty() && !isSearching) {
            TYPE_EMPTY
        } else if (position == 0 && contactList.isEmpty() && isSearching) {
            TYPE_NO_CONTACTS_FOUND
        } else {
            TYPE_CONTACT
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    /**
     * Call this method if the selected address changes from outside this adapter
     */
    fun onSelectedContactChanged(newSelectedContactAddress: String?) {
        val oldSelectedAddress = this.selectedContactAddress
        this.selectedContactAddress = newSelectedContactAddress

        val oldIndex = contactList.indexOfFirstOrNull { it.address == oldSelectedAddress }
        oldIndex?.let { notifyItemChanged(it) }

        val index = contactList.indexOfFirstOrNull { it.address == newSelectedContactAddress }
        index?.let {
            notifyItemChanged(it)
            recyclerView?.scrollToPosition(it)
        }
    }

    // MARK - Private Methods

    private fun onContactViewHolderSelected(index: Int) {
        if (index < contactList.size) {
            var oldSelectedContactIndex: Int? = null
            selectedContactAddress?.let { address ->
                oldSelectedContactIndex = contactList.indexOfFirstOrNull { it.address == address }
            }

            selectedContactAddress = contactList[index].address
            this.onContactSelected(contactList[index])

            oldSelectedContactIndex?.let { notifyItemChanged(it) }
            notifyItemChanged(index)
        }
    }

}