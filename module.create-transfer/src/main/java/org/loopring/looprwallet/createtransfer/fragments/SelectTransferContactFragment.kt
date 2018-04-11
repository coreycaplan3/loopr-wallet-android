package org.loopring.looprwallet.createtransfer.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_select_address.*
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.barcode.delegate.BarcodeCaptureDelegate
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.contacts.fragments.contacts.ViewContactsFragment
import org.loopring.looprwallet.core.extensions.findFragmentByTagOrCreate
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.validators.PublicKeyValidator
import org.loopring.looprwallet.createtransfer.R

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: The first step in the transfer process. This fragment allows you to enter an
 * address or select one from your address book.
 */
class SelectTransferContactFragment : BaseFragment(), ViewContactsFragment.OnContactClickedListener,
        SearchViewPresenter.OnSearchViewChangeListener {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_SELECTED_CONTACT = "_SELECTED_CONTACT"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_address

    var selectedContactAddress: String? = null

    var searchQuery: String? = null
        private set

    lateinit var searchItem: MenuItem

    private lateinit var searchViewPresenter: SearchViewPresenter

    private lateinit var viewContactsFragment: ViewContactsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val imageButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
            BarcodeCaptureDelegate.setupBarcodeScanner(it, BarcodeCaptureActivity::class.java, imageButton)
        }

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = false,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )

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

        val searchView = (searchItem.actionView as SearchView)
        searchViewPresenter.setupSearchView(searchItem, searchView)
    }

    override fun onQueryTextGainFocus() {
        viewContactsFragment.resetRecyclerViewPosition()
    }

    override fun onQueryTextChangeListener(searchQuery: String) {
        viewContactsFragment.searchContactsByName(searchQuery)
    }

    override fun onSearchItemExpanded() {
        viewContactsFragment.searchContactsByName("")

        createTransferRecipientInputContainer.visibility = View.GONE
        createTransferContinueButton.visibility = View.GONE
    }

    override fun onSearchItemCollapsed() {
        // By calling onFormChanged we can restore the state of things based on the
        // address input. Meaning, we won't reset the content to display ALL contacts.
        // Instead, the contacts displayed will be NOW based on the address input
        onFormChanged()

        createTransferRecipientInputContainer.visibility = View.VISIBLE
        createTransferContinueButton.visibility = View.VISIBLE

        viewContactsFragment.scrollToSelectedContact(selectedContactAddress)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureDelegate.handleActivityResult(recipientAddressEditText, requestCode, resultCode, data)
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
        outState.putString(KEY_SELECTED_CONTACT, selectedContactAddress)

        searchViewPresenter.onSaveInstanceState(outState)
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

}