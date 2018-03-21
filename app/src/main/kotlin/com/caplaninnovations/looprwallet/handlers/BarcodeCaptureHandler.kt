package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.widget.ImageButton
import android.widget.EditText
import com.caplaninnovations.looprwallet.activities.BarcodeCaptureActivity

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

    fun setupBarcodeScanner(activity: Activity, barcodeScannerButton: ImageButton) {
        barcodeScannerButton.setOnClickListener {
            val intent = Intent(it.context, BarcodeCaptureActivity::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE_START_BARCODE_ACTIVITY)
        }
    }

    fun handleActivityResult(editText: EditText, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_START_BARCODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra(BarcodeCaptureActivity.KEY_BARCODE_VALUE)
            editText.setText(value)
        }
    }

}