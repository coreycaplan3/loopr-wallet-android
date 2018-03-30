package com.caplaninnovations.looprwallet.fragments.transfers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.animations.ToolbarToSearchAnimation
import com.caplaninnovations.looprwallet.dialogs.CreateContactDialog
import com.caplaninnovations.looprwallet.extensions.findFragmentByTagOrCreate
import com.caplaninnovations.looprwallet.extensions.logd
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.contacts.ViewContactsFragment
import com.caplaninnovations.looprwallet.handlers.BarcodeCaptureHandler
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import com.caplaninnovations.looprwallet.validators.PublicKeyValidator
import kotlinx.android.synthetic.main.barcode_button.*
import kotlinx.android.synthetic.main.fragment_select_address.*

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: The first step in the transfer process. This fragment allows you to enter an
 * address or select one from your address book.
 */
class SelectContactFragment : BaseFragment(), ViewContactsFragment.OnContactClickedListener {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_SEARCH_QUERY = "_SEARCH_QUERY"
        private const val KEY_SELECTED_CONTACT = "_SELECTED_CONTACT"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_address

    private var searchQuery: String? = null
    private lateinit var searchItem: MenuItem

    private var selectedContactAddress: String? = null

    private lateinit var viewContactsFragment: ViewContactsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { BarcodeCaptureHandler.setupBarcodeScanner(it, barcodeScannerButton) }

        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        selectedContactAddress = savedInstanceState?.getString(KEY_SELECTED_CONTACT)

        validatorList = listOf(PublicKeyValidator(recipientAddressInputLayout, this::onFormChanged))

        viewContactsFragment = childFragmentManager.findFragmentByTagOrCreate(ViewContactsFragment.TAG) {
            val fragment = ViewContactsFragment()
            childFragmentManager.beginTransaction()
                    .replace(R.id.viewContactsFragmentContainer, fragment, ViewContactsFragment.TAG)
                    .commit()

            return@findFragmentByTagOrCreate fragment
        }

        viewContactsFragment.onContactClickedListener = this

        viewContactsFragment.selectedContactAddress = selectedContactAddress

        viewContactsFragment.searchContactsByAddress(recipientAddressEditText.text.toString())

        addContactButton.setOnClickListener {
            val currentAddress = recipientAddressEditText.text.toString()
            CreateContactDialog.create(currentAddress).show(fragmentManager, CreateContactDialog.TAG)
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
        viewContactsFragment.scrollToSelectedContact(selectedContactAddress)
    }

    override fun onContactSelected(contact: Contact) {
        // We set the text on the EditText, since it'll trigger #onFormChanged
        recipientAddressEditText.setText(contact.address)
        if (searchItem.isActionViewExpanded) {
            searchItem.collapseActionView()
        }
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

        if (isValid) {
            val contact = viewContactsFragment.getContactByAddress(recipientAddress)
            selectedContactAddress = contact?.address

            addContactButton.visibility = when (contact) {
                null ->
                    // We can't find the contact, so we can allow the user to add this new address
                    View.VISIBLE
                else -> View.GONE
            }

            recipientName = contact?.name ?: str(R.string.unknown)
        } else {
            selectedContactAddress = null
            recipientName = null
            addContactButton.visibility = View.GONE
        }

        viewContactsFragment.onSelectedContactChanged(selectedContactAddress)

        viewContactsFragment.searchContactsByAddress(recipientAddress)

        recipientNameLabel?.text = when {
            recipientName != null -> str(R.string.formatter_recipient).format(recipientName)
            else -> null
        }

        createTransferContinueButton.isEnabled = isValid
    }

    private fun setupSearchView(searchItem: MenuItem, searchView: SearchView) {

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewContactsFragment.resetRecyclerViewPosition()
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

                viewContactsFragment.searchContactsByName(newText)

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

                viewContactsFragment.scrollToSelectedContact(selectedContactAddress)

                if (item.isActionViewExpanded) {
                    ToolbarToSearchAnimation.animateToToolbar(
                            fragment = this@SelectContactFragment,
                            numberOfMenuIcon = 1,
                            containsOverflow = false
                    )
                }
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Called when SearchView is expanding
                logd("Expanding MenuItem...")

                viewContactsFragment.searchContactsByName("")

                createTransferRecipientInputContainer.visibility = View.GONE
                createTransferContinueButton.visibility = View.GONE

                ToolbarToSearchAnimation.animateToSearch(
                        fragment = this@SelectContactFragment,
                        numberOfMenuIcon = 1,
                        containsOverflow = false
                )
                return true
            }
        })

    }

}