package com.caplaninnovations.looprwallet.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.os.bundleOf
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.handlers.BarcodeCaptureHandler
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.repositories.user.ContactsRepository
import com.caplaninnovations.looprwallet.validators.ContactNameValidator
import com.caplaninnovations.looprwallet.validators.PublicKeyValidator
import kotlinx.android.synthetic.main.barcode_button.*
import kotlinx.android.synthetic.main.dialog_create_contact.*

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To allow the user to create a new contact for their address book.
 *
 */
class CreateContactDialog : BaseBottomSheetDialog() {

    companion object {
        val TAG: String = CreateContactDialog::class.java.simpleName

        private const val KEY_ADDRESS = "_ADDRESS"

        fun create(address: String?): CreateContactDialog {
            return CreateContactDialog().apply {
                arguments = bundleOf(KEY_ADDRESS to address)
            }
        }
    }

    private var repository: ContactsRepository? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = LooprWalletApp.dagger.walletClient.getCurrentWallet() ?: return null
            return ContactsRepository(wallet).apply { field = this }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_create_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { BarcodeCaptureHandler.setupBarcodeScanner(it, barcodeScannerButton) }

        if (savedInstanceState == null) {
            contactAddressEditText.setText(arguments?.getString(KEY_ADDRESS))
        }

        validatorList = listOf(
                ContactNameValidator(contactNameInputLayout, this::onFormChanged),
                PublicKeyValidator(contactAddressInputLayout, this::onFormChanged)
        )

        createContactButton.setOnClickListener {
            val contactName = contactNameEditText.text.toString()
            val address = contactAddressEditText.text.toString()
            val contact = Contact(address, contactName)

            repository?.let {
                it.add(contact)
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BarcodeCaptureHandler.handleActivityResult(contactAddressEditText, requestCode, resultCode, data)
    }

    override fun onFormChanged() {
        createContactButton.isEnabled = isAllValidatorsValid()
    }

}