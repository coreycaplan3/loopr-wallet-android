package org.loopring.looprwallet.homemywallet.dialogs

import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_show_barcode.*
import org.loopring.looprwallet.barcode.activities.ViewBarcodeActivity
import org.loopring.looprwallet.barcode.utilities.BarcodeUtility
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homemywallet.R
import kotlin.math.roundToInt

/**
 * Created by Corey on 4/17/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class: A dialog for displaying a barcode, typically for your private key and address
 */
class ShowBarcodeDialog : BaseBottomSheetDialog() {

    companion object {

        val TAG: String = ShowBarcodeDialog::class.java.simpleName

        private const val KEY_TEXT_VALUE = "_TEXT_VALUE"

        fun getInstance(value: String) = ShowBarcodeDialog().apply {
            arguments = bundleOf(KEY_TEXT_VALUE to value)
        }

    }

    override val layoutResource: Int
        get() = R.layout.dialog_show_barcode

    private val barcodeText: String by lazy {
        arguments?.getString(KEY_TEXT_VALUE)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val dimensions = resources.getDimension(R.dimen.qr_code_dimensions).roundToInt()
            val bitmap = BarcodeUtility.encodeTextToBitmap(barcodeText, dimensions)
            barcodeImage.setImageBitmap(bitmap)
            barcodeImage.setOnClickListener {
                ViewBarcodeActivity.route(activity!!, str(R.string.my_private_key), barcodeText)
            }

            barcodeLabel.text = barcodeText
        } catch (e: Throwable) {
            barcodeLabel.setText(R.string.error_creating_qr_code)
            loge("Error creating QR code: ", e)
        }
    }
}