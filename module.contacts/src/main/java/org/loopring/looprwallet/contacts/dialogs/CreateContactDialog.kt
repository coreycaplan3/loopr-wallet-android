package org.loopring.looprwallet.contacts.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_create_contact.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.core.repositories.contacts.ContactsRepository
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.models.android.architecture.IO
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

    private val repository by lazy { ContactsRepository() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barcodeButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
        BarcodeCaptureActivity.setupBarcodeScanner(this, barcodeButton, arrayOf(BarcodeCaptureActivity.TYPE_PUBLIC_KEY))

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

            async(UI) {
                async(IO) {
                    val contact = Contact(address, contactName)
                    repository.add(contact)
                }.await()

                dismissAllowingStateLoss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BarcodeCaptureActivity.handleActivityResult(contactAddressEditText, requestCode, resultCode, data)
    }

    override fun onFormChanged() {
        createContactButton.isEnabled = isAllValidatorsValid()
    }

}