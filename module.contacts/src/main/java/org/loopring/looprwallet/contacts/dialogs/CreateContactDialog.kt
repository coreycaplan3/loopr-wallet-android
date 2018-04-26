package org.loopring.looprwallet.contacts.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_create_contact.*
import org.loopring.looprwallet.barcode.activities.QRCodeCaptureActivity
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.contacts.repositories.ContactsRepository
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.models.contact.Contact
import org.loopring.looprwallet.core.validators.ContactNameValidator
import org.loopring.looprwallet.core.validators.PublicKeyValidator

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

        fun getInstance(address: String?): CreateContactDialog {
            return CreateContactDialog().apply {
                arguments = bundleOf(KEY_ADDRESS to address)
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.dialog_create_contact

    private var repository: ContactsRepository? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            return ContactsRepository(wallet).apply { field = this }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
        QRCodeCaptureActivity.setupBarcodeScanner(this, barcodeButton, arrayOf(QRCodeCaptureActivity.TYPE_PUBLIC_KEY))

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
        QRCodeCaptureActivity.handleActivityResult(contactAddressEditText, requestCode, resultCode, data)
    }

    override fun onFormChanged() {
        createContactButton.isEnabled = isAllValidatorsValid()
    }

}