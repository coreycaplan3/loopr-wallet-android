package com.caplaninnovations.looprwallet.fragments.transfers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.CreateContactActivity
import com.caplaninnovations.looprwallet.adapters.contacts.ContactsAdapter
import com.caplaninnovations.looprwallet.animations.ToolbarToSearchAnimation
import com.caplaninnovations.looprwallet.extensions.indexOfFirstOrNull
import com.caplaninnovations.looprwallet.extensions.logd
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.BarcodeCaptureHandler
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import com.caplaninnovations.looprwallet.validators.PublicKeyValidator
import com.caplaninnovations.looprwallet.viewmodels.LooprWalletViewModelFactory
import com.caplaninnovations.looprwallet.viewmodels.contacts.ContactsByAddressViewModel
import com.caplaninnovations.looprwallet.viewmodels.contacts.ContactsByNameViewModel
import io.realm.RealmList
import kotlinx.android.synthetic.main.barcode_button.*
import kotlinx.android.synthetic.main.fragment_select_address.*

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class SelectAddressFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_SEARCH_QUERY = "_SEARCH_QUERY"
        private const val KEY_SELECTED_CONTACT = "_SELECTED_CONTACT"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_address

    private lateinit var adapter: ContactsAdapter

    private var searchQuery: String? = null
    private lateinit var searchItem: MenuItem

    private var contactList: RealmList<Contact>? = null

    private val contactsByAddressViewModel: ContactsByAddressViewModel?
        get() {
            val wallet = walletClient.getCurrentWallet()
            return wallet?.let { LooprWalletViewModelFactory.get(this, it) }
        }

    private val contactsByNameViewModel: ContactsByNameViewModel?
        get() {
            val wallet = walletClient.getCurrentWallet()
            return wallet?.let { LooprWalletViewModelFactory.get(this, it) }
        }

    private var selectedContactAddress: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { BarcodeCaptureHandler.setupBarcodeScanner(it, barcodeScannerButton) }

        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        selectedContactAddress = savedInstanceState?.getString(KEY_SELECTED_CONTACT)

        val layoutManager = LinearLayoutManager(context)
        contactsRecyclerView.layoutManager = layoutManager
        contactsRecyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        adapter = ContactsAdapter(selectedContactAddress, this::onContactSelected)
        contactsRecyclerView.adapter = adapter

        queryRealmForContactsByAddress()

        validatorList = listOf(PublicKeyValidator(recipientAddressInputLayout, this::onFormChanged))

        addContactButton.setOnClickListener {
            val currentAddress = recipientAddressEditText.text.toString()
            startActivity(CreateContactActivity.createIntent(currentAddress))
        }

        createTransferContinueButton.setOnClickListener {
            val currentAddress = recipientAddressEditText.text.toString()
            pushFragmentTransaction(
                    CreateTransferAmountFragment.createInstance(currentAddress),
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
                // Wait fo the item and fragment to be setup
                searchItem.expandActionView()
                (searchItem.actionView as SearchView).setQuery(searchQuery, false)
            }, 300)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureHandler.handleActivityResult(recipientAddressEditText, requestCode, resultCode, data)
    }


    override fun onHideKeyboard() {
        scrollToSelectedContact()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val queryString = searchQuery?.let {
            if (it.trim().isEmpty()) null
            else it
        }
        outState.putString(KEY_SEARCH_QUERY, queryString)
        outState.putString(KEY_SELECTED_CONTACT, selectedContactAddress)
    }

    // MARK - Private Methods

    override fun onFormChanged() {
        val isValid = isAllValidatorsValid()
        val recipientAddress = recipientAddressEditText.text.toString()
        val recipientName: String?

        queryRealmForContactsByAddress()

        // If it's not empty, we're searching!
        adapter.isSearching = !TextUtils.isEmpty(recipientAddress)
        adapter.notifyDataSetChanged()

        if (isValid) {
            val contact = contactList?.find { it.address == recipientAddress }
            selectedContactAddress = contact?.address

            addContactButton.visibility = if (contact == null) {
                // We cannot find the contact, so we can allow the user to add this new address
                View.VISIBLE
            } else {
                View.GONE
            }

            recipientName = contact?.name ?: str(R.string.unknown)
        } else {
            selectedContactAddress = null
            recipientName = null
            addContactButton.visibility = View.GONE
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
     * Called from the [adapter] when a user selected a contact
     */
    private fun onContactSelected(contact: Contact) {
        // We set the text on the EditText, since it'll trigger #onFormChanged
        recipientAddressEditText.setText(contact.address)
        if (searchItem.isActionViewExpanded) {
            searchItem.collapseActionView()
        }
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

                queryRealmForContactsByName(newText)

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

                // By calling onFormChanged we can restore the state of things based on the
                // address input
                onFormChanged()

                createTransferRecipientInputContainer.visibility = View.VISIBLE
                createTransferContinueButton.visibility = View.VISIBLE

                scrollToSelectedContact()

                if (item.isActionViewExpanded) {
                    ToolbarToSearchAnimation.animateToToolbar(
                            fragment = this@SelectAddressFragment,
                            numberOfMenuIcon = 1,
                            containsOverflow = false
                    )
                }
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Called when SearchView is expanding
                logd("Expanding MenuItem...")

                queryRealmForContactsByName("")

                createTransferRecipientInputContainer.visibility = View.GONE
                createTransferContinueButton.visibility = View.GONE

                ToolbarToSearchAnimation.animateToSearch(
                        fragment = this@SelectAddressFragment,
                        numberOfMenuIcon = 1,
                        containsOverflow = false
                )
                return true
            }
        })

    }

    private fun queryRealmForContactsByName(name: String) {
        contactsByNameViewModel?.getAllContactsByName(
                this,
                name,
                { adapter.updateData(it) }
        )
    }

    private fun queryRealmForContactsByAddress() {
        val recipientAddress = recipientAddressEditText.text.toString()

        contactsByAddressViewModel?.getAllContactsByAddress(
                this,
                recipientAddress,
                { adapter.updateData(it) }
        )
    }

    private fun scrollToSelectedContact() {
        selectedContactAddress?.let { address ->
            val index = contactList?.indexOfFirstOrNull { it.address == address }
            index?.let { contactsRecyclerView?.scrollToPosition(it) }
        }
    }

}