package org.loopring.looprwallet.addtoken.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import kotlinx.android.synthetic.main.dialog_add_token.*
import org.loopring.looprwallet.addtoken.R
import org.loopring.looprwallet.barcode.activities.QRCodeCaptureActivity
import org.loopring.looprwallet.barcode.activities.QRCodeCaptureActivity.Companion.TYPE_PUBLIC_KEY
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.validators.PublicKeyValidator
import org.loopring.looprwallet.core.validators.TokenDecimalValidator
import org.loopring.looprwallet.core.validators.TokenNameValidator
import org.loopring.looprwallet.core.validators.TokenTickerValidator
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.TokenRetrieverViewModel
import java.io.IOException

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 *
 */
class AddTokenDialog : BaseBottomSheetDialog() {

    override val layoutResource: Int
        get() = R.layout.dialog_add_token

    private val tokenRetrieverViewModel by lazy {
        LooprViewModelFactory.get<TokenRetrieverViewModel>(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionViewModel(tokenRetrieverViewModel, str(R.string.adding_token)) {
            return@setupTransactionViewModel when (it) {
                is IOException -> str(R.string.error_no_connection)
                else -> str(R.string.error_unknown)
            }
        }

        dialog?.setTitle(R.string.add_custom_token)

        activity?.let {
            val barcodeScannerButton = view.findViewById<ImageButton>(R.id.barcodeScannerButton)
            QRCodeCaptureActivity.setupBarcodeScanner(this, barcodeScannerButton, arrayOf(TYPE_PUBLIC_KEY))
        }

        validatorList = listOf(
                PublicKeyValidator(addTokenContractAddressInputLayout, ::onFormChanged),
                TokenNameValidator(addTokenNameInputLayout, ::onFormChanged),
                TokenTickerValidator(addTokenTickerInputLayout, ::onFormChanged),
                TokenDecimalValidator(addTokenTickerInputLayout, ::onFormChanged)
        )

        addCustomTokenButton.setOnClickListener { onAddCustomTokenClick() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        QRCodeCaptureActivity.handleActivityResult(addTokenContractAddressEditText, requestCode, resultCode, data)
    }

    override fun onFormChanged() {
        addCustomTokenButton.isEnabled = isAllValidatorsValid()
    }

    // MARK - Private Methods

    private fun onAddCustomTokenClick() {
        val contractAddress = addTokenContractAddressEditText.text.toString()
        tokenRetrieverViewModel.getTokenInfoFromNetworkAndAdd(contractAddress)
    }

}