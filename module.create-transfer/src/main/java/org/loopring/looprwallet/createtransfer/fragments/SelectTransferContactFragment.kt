package org.loopring.looprwallet.createtransfer.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.os.bundleOf
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.fragment_select_transfer_contact.*
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity.Companion.TYPE_PUBLIC_KEY
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.contacts.fragments.ViewContactsFragment
import org.loopring.looprwallet.contacts.repositories.ContactsRepository
import org.loopring.looprwallet.core.extensions.findFragmentByTagOrCreate
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility
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
        SearchFragment, OnSearchViewChangeListener {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_SELECTED_CONTACT = "_SELECTED_CONTACT"

        private const val KEY_DEFAULT_ADDRESS = "_DEFAULT_ADDRESS"

        fun getInstance(defaultAddress: String?) = SelectTransferContactFragment().apply {
            arguments = bundleOf(KEY_DEFAULT_ADDRESS to defaultAddress)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_select_transfer_contact

    var selectedContactAddress: String? = null

    lateinit var searchItem: MenuItem

    override lateinit var searchViewPresenter: SearchViewPresenter

    private lateinit var viewContactsFragment: ViewContactsFragment

    private val repository by lazy {
        ContactsRepository()
    }

    private var allContacts: LiveData<OrderedRealmCollection<Contact>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = false,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )

        toolbarDelegate?.onCreateOptionsMenu = createOptionsMenu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeScannerButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
        BarcodeCaptureActivity.setupBarcodeScanner(this, barcodeScannerButton, arrayOf(TYPE_PUBLIC_KEY))

        selectedContactAddress = when (savedInstanceState) {
            null -> arguments?.getString(KEY_DEFAULT_ADDRESS)
            else -> savedInstanceState.getString(KEY_SELECTED_CONTACT)
        }

        viewContactsFragment = childFragmentManager.findFragmentByTagOrCreate(ViewContactsFragment.TAG) {
            val fragment = ViewContactsFragment.getSearchableInstance()
            childFragmentManager.beginTransaction()
                    .replace(R.id.viewContactsFragmentContainer, fragment, ViewContactsFragment.TAG)
                    .commitNow()

            return@findFragmentByTagOrCreate fragment
        }.also {
            it.onContactClickedListener = this
            it.selectedContactAddress = selectedContactAddress
            it.searchContactsByAddress(recipientAddressEditText.text.toString())
        }

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

    private val createOptionsMenu: (Toolbar?) -> Unit = {
        it?.menu?.clear()
        it?.inflateMenu(R.menu.search_contacts_menu)

        if (it != null) {
            searchItem = it.menu.findItem(R.id.menu_search_contacts)
            val searchView = (searchItem.actionView as SearchView)
            searchViewPresenter.setupSearchView(searchItem, searchView)
        }
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

        BarcodeCaptureActivity.handleActivityResult(recipientAddressEditText, requestCode, resultCode, data)
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

        if (isValid) {
            val contact = repository.getContactByAddressNow(recipientAddress)
            setupContact(contact)

            selectedContactAddress = recipientAddress
        } else {
            selectedContactAddress = null
            addContactButton.visibility = View.GONE
        }

        viewContactsFragment.onSelectedContactChanged(selectedContactAddress)

        viewContactsFragment.searchContactsByAddress(recipientAddress)

        createTransferContinueButton.isEnabled = isValid
    }

    private fun setupContact(contact: Contact?) {

        fun bindContact(contact: Contact?) {
            if (contact != null) {
                recipientNameLabel?.text = ApplicationUtility.str(R.string.formatter_recipient).format(contact.name)
                addContactButton.visibility = View.GONE
            } else {
                recipientNameLabel?.text = ApplicationUtility.str(R.string.unknown)
                addContactButton.visibility = View.VISIBLE
            }
        }

        bindContact(contact)

        val selectedContactAddress = selectedContactAddress ?: return
        allContacts = repository.getAllContactsByAddress(selectedContactAddress)
        allContacts?.observe(fragmentViewLifecycleFragment!!, Observer<OrderedRealmCollection<Contact>> {
            if (it != null && it.isValid && it.size > 0) {
                bindContact(it[0])
            }
        })
    }

}