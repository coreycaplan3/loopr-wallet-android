/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.loopring.looprwallet.barcode.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_barcode_code_capture.*
import org.loopring.looprwallet.barcode.R
import org.loopring.looprwallet.barcode.views.BarcodeGraphicTracker
import org.loopring.looprwallet.barcode.views.BarcodeTrackerFactory
import org.loopring.looprwallet.barcode.views.CameraSource
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.delegates.PermissionDelegate
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.web3j.crypto.WalletUtils
import java.io.IOException

/**
 * This activity detects QR codes and displays the value with the rear facing camera. During
 * detection, overlay graphics are drawn to indicate the position, size, and ID of each barcode.
 */
class BarcodeCaptureActivity : BaseActivity(), BarcodeGraphicTracker.BarcodeUpdateListener {

    companion object {

        private const val REQUEST_CODE_HANDLE_GMS_ERROR = 9001

        /**
         * Key for the supported types of QR codes in this activity
         */
        private const val KEY_ALLOWED_TYPES = "_SUPPORTED_TYPES"

        const val KEY_TYPE = "_TYPE"

        const val TYPE_PUBLIC_KEY = "_PUBLIC_KEY"
        const val TYPE_PRIVATE_KEY = "_PRIVATE_KEY"
        const val TYPE_TRADING_PAIR = "_TRADING_PAIR"

        const val REQUEST_CODE_START_BARCODE_ACTIVITY = 1323

        const val KEY_BARCODE_VALUE = "_BARCODE_VALUE"

        /**
         * Sets up the barcode scanner to work with the provided [fragment].
         * @param fragment The fragment that should be set up with this barcode scanner
         * @param barcodeScannerButton The [ImageButton] that whose click listener will start the
         * barcode scanner
         * @param allowedTypes The types of entities that can be scanned by the barcode scanner
         * @see TYPE_PUBLIC_KEY
         * @see TYPE_PRIVATE_KEY
         * @see TYPE_TRADING_PAIR
         * @throws IllegalArgumentException If [allowedTypes] is empty
         */
        fun setupBarcodeScanner(fragment: Fragment, barcodeScannerButton: ImageButton, allowedTypes: Array<String>) {
            if (allowedTypes.isEmpty()) {
                throw IllegalArgumentException("allowedTypes cannot be empty!")
            }

            barcodeScannerButton.setOnClickListener {
                route(fragment, allowedTypes)
            }
        }

        fun handleActivityResult(editText: EditText, requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == REQUEST_CODE_START_BARCODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
                val value = data?.getStringExtra(KEY_BARCODE_VALUE)
                editText.setText(value)
            }
        }

        /**
         * Handles the activity result after returning back from this [BarcodeCaptureActivity].
         *
         * @param requestCode The activity's request code
         * @param resultCode The activity's result code
         * @param data The data returned by the activity
         * @param onValidResult A function that is invoked if the [requestCode] and [resultCode]
         * are valid. It takes the type of result (1st) and the value (2nd) as parameters
         */
        inline fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, onValidResult: (String, String) -> Unit) {
            if (requestCode == REQUEST_CODE_START_BARCODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
                val type = data?.getStringExtra(KEY_TYPE)
                val value = data?.getStringExtra(KEY_BARCODE_VALUE)

                if (type != null && value != null) {
                    onValidResult(type, value)
                }
            }
        }

        fun route(fragment: Fragment, allowedTypes: Array<out String>) {
            fragment.startActivityForResult(getIntent(allowedTypes), REQUEST_CODE_START_BARCODE_ACTIVITY)
        }

        private fun getIntent(allowedTypes: Array<out String>): Intent {
            return Intent(CoreLooprWalletApp.application, BarcodeCaptureActivity::class.java)
                    .putExtra(KEY_ALLOWED_TYPES, allowedTypes)
        }

        private fun putActivityResultAndFinish(activity: BaseActivity, type: String, barcodeResult: String) {
            val data = Intent()
            data.putExtra(KEY_BARCODE_VALUE, barcodeResult)
            data.putExtra(KEY_TYPE, type)
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }

    }

    private var mCameraSource: CameraSource? = null

    // helper objects for detecting taps and pinches.
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector? = null

    override val contentViewRes: Int
        get() = R.layout.activity_barcode_code_capture

    override val isSignInRequired: Boolean
        get() = false

    private val allowedTypes: Array<String> by lazy {
        intent.getStringArrayExtra(KEY_ALLOWED_TYPES)
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        graphicOverlay.snackbar(R.string.qr_code_scanner_instructions, Snackbar.LENGTH_LONG)
    }

    override fun getAllPermissionHandlers(): List<PermissionDelegate> {
        return listOf(
                PermissionDelegate(
                        activity = this,
                        permission = Manifest.permission.CAMERA,
                        requestCode = PermissionDelegate.REQUEST_CODE_CAMERA,
                        onPermissionGranted = { createCameraSource() },
                        onPermissionDenied = { onRequestCameraPermissionFailed() }
                )
        )
    }

    @SuppressLint("Range")
    override fun onBarcodeDetected(barcode: Barcode) {
        val isAddressAllowed = allowedTypes.any { it == TYPE_PUBLIC_KEY }
        val isPrivateKeyAllowed = allowedTypes.any { it == TYPE_PRIVATE_KEY }
        val isAddressAccepted = allowedTypes.any { it == TYPE_TRADING_PAIR }

        val value = barcode.rawValue
        when {
            isAddressAllowed && WalletUtils.isValidAddress(value) ->
                putActivityResultAndFinish(this, TYPE_PUBLIC_KEY, value)

            isPrivateKeyAllowed && WalletUtils.isValidPrivateKey(value) ->
                putActivityResultAndFinish(this, TYPE_PRIVATE_KEY, value)

            isAddressAccepted && TradingPair.isValidMarket(value) ->
                putActivityResultAndFinish(this, TYPE_TRADING_PAIR, value)
            else -> {
                val message = String.format(str(R.string.error_invalid_qr_code), value)
                cameraSourcePreview.snackbar(message, Snackbar.LENGTH_LONG)
            }
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val b = scaleGestureDetector!!.onTouchEvent(e)

        val c = gestureDetector!!.onTouchEvent(e)

        return b || c || super.onTouchEvent(e)
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private fun createCameraSource() {
        val context = applicationContext

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to transfer_receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        val barcodeDetector = BarcodeDetector.Builder(context).build()
        val barcodeFactory = BarcodeTrackerFactory(graphicOverlay, this)
        barcodeDetector.setProcessor(
                MultiProcessor.Builder(barcodeFactory).build()
        )

        if (!barcodeDetector.isOperational) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            logw("Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            @Suppress("DEPRECATION")
            val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowStorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                logw(getString(R.string.low_storage_error))
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        mCameraSource = CameraSource.Builder(applicationContext, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .build()
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        cameraSourcePreview?.stop()
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraSourcePreview?.release()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // Check that the device has play services available.
        val code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, code, REQUEST_CODE_HANDLE_GMS_ERROR)
                    .show()
        }

        mCameraSource?.let {
            try {
                cameraSourcePreview?.start(it, graphicOverlay)
            } catch (se: SecurityException) {
                loge("The app lacks permission to start the camera", se)
            } catch (ioe: IOException) {
                loge("Unable to start camera source.", ioe)
                it.release()
                cameraSourcePreview?.snackbar(R.string.error_starting_camera, Snackbar.LENGTH_LONG)
                mCameraSource = null
            }
        }
    }

    /**
     * Called when the user denies the camera permission
     */
    private fun onRequestCameraPermissionFailed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok) { _, _ -> finish() }
                .show()
    }


    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        // Find tap point in preview frame coordinates.
        val location = IntArray(2)
        graphicOverlay!!.getLocationOnScreen(location)
        val x = (rawX - location[0]) / graphicOverlay!!.widthScaleFactor
        val y = (rawY - location[1]) / graphicOverlay!!.heightScaleFactor

        // Find the barcode whose center is closest to the tapped point.
        var bestBarcode: Barcode? = null
        var bestDistance = java.lang.Float.MAX_VALUE
        for (graphic in graphicOverlay!!.graphics) {
            val barcode = graphic.barcode
            if (barcode.boundingBox.contains(x.toInt(), y.toInt())) {
                // Exact hit, no need to keep looking.
                bestBarcode = barcode
                break
            }
            val dx = x - barcode.boundingBox.centerX()
            val dy = y - barcode.boundingBox.centerY()
            val distance = dx * dx + dy * dy  // actually squared distance
            if (distance < bestDistance) {
                bestBarcode = barcode
                bestDistance = distance
            }
        }

        return bestBarcode != null
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         *
         *
         * Once a scale has ended, [ScaleGestureDetector.getFocusX]
         * and [ScaleGestureDetector.getFocusY] will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            mCameraSource!!.doZoom(detector.scaleFactor)
        }
    }

}