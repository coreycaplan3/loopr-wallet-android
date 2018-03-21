package com.caplaninnovations.looprwallet.fragments.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.BarcodeCaptureHandler
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.validators.ContactNameValidator
import com.caplaninnovations.looprwallet.validators.PublicKeyValidator
import kotlinx.android.synthetic.main.barcode_button.*
import kotlinx.android.synthetic.main.fragment_create_contact.*

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateContactFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateContactFragment::class.java.simpleName

        private const val KEY_ADDRESS = "_ADDRESS"

        fun createInstance(address: String?): CreateContactFragment {
            return CreateContactFragment().apply {
                arguments = Bundle().apply { putString(KEY_ADDRESS, address) }
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_contact

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setTitle(R.string.create_contact)

        activity?.let { BarcodeCaptureHandler.setupBarcodeScanner(it, barcodeScannerButton) }

        if (savedInstanceState == null) {
            contactAddressEditText.setText(arguments?.getString(KEY_ADDRESS))
        }

        validatorList = listOf(
                ContactNameValidator(contactNameInputLayout, this::onFormChanged),
                PublicKeyValidator(contactAddressInputLayout, this::onFormChanged)
        )

        createContactButton.setOnClickListener {
            val activity = activity as? BaseActivity
            activity?.let {
                val contactName = contactNameEditText.text.toString()
                val address = contactAddressEditText.text.toString()
                val contact = Contact(address, contactName)

                // TODO


                it.setResult(Activity.RESULT_OK)
                it.finish()
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