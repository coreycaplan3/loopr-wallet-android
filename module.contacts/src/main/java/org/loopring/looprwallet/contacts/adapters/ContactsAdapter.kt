package org.loopring.looprwallet.contacts.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.guard
import org.loopring.looprwallet.core.extensions.indexOfFirstOrNull
import org.loopring.looprwallet.core.models.contact.Contact

/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsAdapter(var selectedContactAddress: String?,
                      private var onContactSelected: (Contact) -> Unit
) : BaseRealmAdapter<Contact>() {

    var isSearching = false

    override val totalItems: Int? = null

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view: View
        return when {
            isSearching -> {
                view = inflater.inflate(R.layout.view_holder_no_contact_found, parent, false)
                NoContactsFoundViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.view_holder_contact_empty, parent, false)
                EmptyContactsViewHolder(view)
            }
        }
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_contact, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: Contact?) {

        item?.guard { } ?: return
        (holder as? ContactsViewHolder)?.bind(item, selectedContactAddress) {
            onContactViewHolderSelected(it)
        }
    }

    /**
     * Call this method if the selected address changes from outside this adapter.
     * This method is only responsible for updating the data-model so the UI changes appropriately
     */
    fun onSelectedContactChanged(newSelectedContactAddress: String?) {
        this.selectedContactAddress = newSelectedContactAddress

        val index = data?.indexOfFirstOrNull { it.address == newSelectedContactAddress }
        index?.let {
            notifyItemChanged(it)
        }
    }

    // MARK - Private Methods

    private fun onContactViewHolderSelected(index: Int) {
        val contactList = data ?: return

        if (index < contactList.size) {
            var oldSelectedContactIndex: Int? = null
            selectedContactAddress?.let { address ->
                oldSelectedContactIndex = contactList.indexOfFirstOrNull { it.address == address }
            }

            selectedContactAddress = contactList[index].address
            this.onContactSelected(contactList[index])

            if (selectedContactAddress != null) {
                oldSelectedContactIndex?.let { notifyItemChanged(it) }
                notifyItemChanged(index)
            }
        }
    }

}