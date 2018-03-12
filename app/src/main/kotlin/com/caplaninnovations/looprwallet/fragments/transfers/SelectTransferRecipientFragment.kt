package com.caplaninnovations.looprwallet.fragments.transfers

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_select_transfer_recipient.*
import com.caplaninnovations.looprwallet.adapters.contacts.ContactsAdapter
import com.caplaninnovations.looprwallet.animations.ToolbarToSearchAnimation
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.utilities.indexOfFirstOrNull
import com.caplaninnovations.looprwallet.utilities.logd
import com.caplaninnovations.looprwallet.utilities.str
import com.caplaninnovations.looprwallet.validators.PublicKeyValidator

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class SelectTransferRecipientFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_SEARCH_QUERY = "_SEARCH_QUERY"
        private const val KEY_SELECTED_CONTACT = "_SELECTED_CONTACT"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_transfer_recipient

    private lateinit var adapter: ContactsAdapter

    private var searchQuery: String? = null
    private lateinit var searchItem: MenuItem

    private val contactList = listOf(
            Contact("0xabcdef12345678abcdef12345678abcdef123451", "Brad"),
            Contact("0xabcdef12345678abcdef12345678abcdef123452", "Brandon"),
            Contact("0xabcdef12345678abcdef12345678abcdef123453", "Corey"),
            Contact("0xabcdef12345678abcdef12345678abcdef123454", "Daniel"),
            Contact("0xabcdef12345678abcdef12345678abcdef123455", "Isaac"),
            Contact("0xabcdef12345678abcdef12345678abcdef123456", "Joel"),
            Contact("0xabcdef12345678abcdef12345678abcdef123457", "Lee"),
            Contact("0xabcdef12345678abcdef12345678abcdef123458", "Paige")
    )

    private var selectedContactAddress: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        selectedContactAddress = savedInstanceState?.getString(KEY_SELECTED_CONTACT)

        val layoutManager = LinearLayoutManager(context)
        contactsRecyclerView.layoutManager = layoutManager
        contactsRecyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        adapter = ContactsAdapter(contactList, selectedContactAddress, this::onContactSelected)
        contactsRecyclerView.adapter = adapter

        validatorList = listOf(PublicKeyValidator(recipientAddressInputLayout, this::onFormChanged))

        createTransferContinueButton.setOnClickListener {
            val recipientAddress = recipientAddressEditText.text.toString()

            pushFragmentTransaction(
                    CreateTransferAmountFragment.createInstance(recipientAddress),
                    CreateTransferAmountFragment.TAG
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_contacts_menu, menu)

        searchItem = menu.findItem(R.id.menu_search_contacts)

        (searchItem.actionView as? SearchView)?.let { setupSearchView(searchItem, it) }

        if (searchQuery != null) {
            Handler().postDelayed({
                searchItem.expandActionView()
                (searchItem.actionView as SearchView).setQuery(searchQuery, false)
            }, 300)
        }

    }

    override fun onHideKeyboard() {
        scrollToSelectedContact()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val queryString = searchQuery?.let {
            if(it.trim().isEmpty()) null
            else it
        }
        outState.putString(KEY_SEARCH_QUERY, queryString)
        outState.putString(KEY_SELECTED_CONTACT, selectedContactAddress)
    }

    // MARK - Private Methods

    override fun onFormChanged() {
        val isValid = isAllValidatorsValid()

        val recipientName: String? = if (isValid) {
            val recipientAddress = recipientAddressEditText.text.toString()
            val contact = contactList.find { it.address == recipientAddress }
            selectedContactAddress = contact?.address

            contact?.name ?: str(R.string.unknown)
        } else {
            selectedContactAddress = null
            null
        }

        adapter.onSelectedContactChanged(selectedContactAddress)

        recipientNameLabel?.text = if (recipientName != null) {
            String.format(str(R.string.formatter_recipient), recipientName)
        } else {
            null
        }

        createTransferContinueButton.isEnabled = isValid
    }

    /**
     * Called from the adapter when a user selected a contact
     */
    private fun onContactSelected(contact: Contact) {
        // We set the text on the EditText, since it'll trigger #onFormChanged
        recipientAddressEditText.setText(contact.address)
        searchItem.collapseActionView()
    }

    private fun setupSearchView(searchItem: MenuItem, searchView: SearchView) {

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (adapter.itemCount > 0) {
                    contactsRecyclerView?.smoothScrollToPosition(0)
                }
            }
        }

        var wasInitialized = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (wasInitialized) {
                    searchQuery = newText
                } else {
                    wasInitialized = true
                }

                adapter.isSearching = !TextUtils.isEmpty(newText)

                if (TextUtils.isEmpty(newText)) {
                    adapter.contactList = contactList
                } else {
                    adapter.contactList = contactList.filter {
                        it.name.toLowerCase().contains(newText.toLowerCase())
                    }
                }

                adapter.notifyDataSetChanged()

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                logd("Collapsing MenuItem...")

                searchQuery = null

                adapter.contactList = contactList
                adapter.notifyDataSetChanged()

                createTransferRecipientInputContainer.visibility = View.VISIBLE
                createTransferContinueButton.visibility = View.VISIBLE

                scrollToSelectedContact()

                if (item.isActionViewExpanded) {
                    ToolbarToSearchAnimation.animateToToolbar(
                            fragment = this@SelectTransferRecipientFragment,
                            numberOfMenuIcon = 1,
                            containsOverflow = false
                    )
                }
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Called when SearchView is expanding
                logd("Expanding MenuItem...")

                createTransferRecipientInputContainer.visibility = View.GONE
                createTransferContinueButton.visibility = View.GONE

                ToolbarToSearchAnimation.animateToSearch(
                        fragment = this@SelectTransferRecipientFragment,
                        numberOfMenuIcon = 1,
                        containsOverflow = false
                )
                return true
            }
        })

    }

    private fun scrollToSelectedContact() {
        selectedContactAddress?.let { address ->
            val index = contactList.indexOfFirstOrNull { it.address == address }
            index?.let { contactsRecyclerView?.scrollToPosition(it) }
        }
    }

}