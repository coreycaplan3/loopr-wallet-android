package org.loopring.looprwallet.createtransfer.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_select_address.*
import org.loopring.looprwallet.barcode.activities.QRCodeCaptureActivity
import org.loopring.looprwallet.barcode.activities.QRCodeCaptureActivity.Companion.TYPE_PUBLIC_KEY
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.contacts.fragments.ViewContactsFragment
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

        private const val KEY_DEFAULT_ADDRESS = "_DEFAULT_ADDRESS"

        fun getInstance(defaultAddress: String?) = SelectTransferContactFragment().apply {
            arguments = bundleOf(KEY_DEFAULT_ADDRESS to defaultAddress)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_address

    var selectedContactAddress: String? = null

    lateinit var searchItem: MenuItem

    override lateinit var searchViewPresenter: SearchViewPresenter

    private lateinit var viewContactsFragment: ViewContactsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeScannerButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
        QRCodeCaptureActivity.setupBarcodeScanner(this, barcodeScannerButton, arrayOf(TYPE_PUBLIC_KEY))

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = false,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )

        if (savedInstanceState == null) {
            selectedContactAddress = arguments?.getString(KEY_DEFAULT_ADDRESS)
        } else {
            selectedContactAddress = savedInstanceState.getString(KEY_SELECTED_CONTACT)
        }

        viewContactsFragment = childFragmentManager.findFragmentByTagOrCreate(ViewContactsFragment.TAG) {
            val fragment = ViewContactsFragment()
            childFragmentManager.beginTransaction()
                    .replace(R.id.viewContactsFragmentContainer, fragment, ViewContactsFragment.TAG)
                    .commitNow()

            return@findFragmentByTagOrCreate fragment
        }

        viewContactsFragment.onContactClickedListener = this

        viewContactsFragment.selectedContactAddress = selectedContactAddress

        viewContactsFragment.searchContactsByAddress(recipientAddressEditText.text.toString())

        addContactButton.setOnClickListener {
            val currentAddress = recipientAddressEditText.text.toString()
            CreateContactDialog.getInstance(currentAddress).show(fragmentManager, CreateContactDialog.TAG)
        }

        createTransferContinueButton.setOnClickListener {
            val currentAddress = recipientAddressEditText.text.toString()
            pushFragmentTransaction(
                    CreateTransferAmountFragment.getInstance(currentAddress),
                    CreateTransferAmountFragment.TAG
            )
        }

        validatorList = listOf(PublicKeyValidator(recipientAddressInputLayout, this::onFormChanged))
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

        QRCodeCaptureActivity.handleActivityResult(recipientAddressEditText, requestCode, resultCode, data)
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