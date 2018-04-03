package org.loopring.looprwallet.barcode.handlers

import android.app.Activity
import android.content.Intent
import android.widget.EditText
import android.widget.ImageButton
import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object BarcodeCaptureHandler {

    private const val REQUEST_CODE_START_BARCODE_ACTIVITY = 1323

    private const val KEY_BARCODE_VALUE = "_BARCODE_VALUE"

    // TODO refactor after making Capture Module
    fun <T : BaseActivity> setupBarcodeScanner(activity: Activity, barcodeActivityClass: Class<T>, barcodeScannerButton: ImageButton) {
        barcodeScannerButton.setOnClickListener {
            val intent = Intent(it.context, barcodeActivityClass)
            activity.startActivityForResult(intent, REQUEST_CODE_START_BARCODE_ACTIVITY)
        }
    }

    fun putActivityResult(activity: BaseActivity, barcodeResult: String) {
        val data = Intent()
        data.putExtra(KEY_BARCODE_VALUE, barcodeResult)
        activity.setResult(Activity.RESULT_OK, data)
        activity.supportFinishAfterTransition()
    }

    fun handleActivityResult(editText: EditText, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_START_BARCODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra(KEY_BARCODE_VALUE)
            editText.setText(value)
        }
    }

}