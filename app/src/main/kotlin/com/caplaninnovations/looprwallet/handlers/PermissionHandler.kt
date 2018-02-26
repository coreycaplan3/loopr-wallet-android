package com.caplaninnovations.looprwallet.handlers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.IntDef
import android.support.annotation.VisibleForTesting
import android.support.v4.app.ActivityCompat
import com.caplaninnovations.looprwallet.activities.BaseActivity
import kotlin.reflect.KProperty

/**
 * Created by Corey Caplan on 2/9/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To handle the requesting of permissions in a uniform manner.
 *
 * @param activity The activity instance in which the permission will be requested.
 * @param permission The string permission that is being requested
 * @param requestCode The request code that will be used for a call to
 * [ActivityCompat.requestPermissions].
 * @param onPermissionGranted A function that's called when the permission is granted to the app or
 * if it's already granted.
 * @param onPermissionDenied A function that's called when the permission is denied to the app.
 * @param shouldRequestPermissionNow True if we should make a call to [requestPermission]
 * immediately. **NOTE**: You must make a call to [requestPermission] on your own if you set this
 * variable to false. All other functionality will still be handled for you though (when permissions
 * are denied/granted).
 */
open class PermissionHandler(private val activity: BaseActivity,
                             private val permission: String,
                             @Code private val requestCode: Int,
                             private val onPermissionGranted: () -> Unit,
                             private val onPermissionDenied: () -> Unit,
                             val shouldRequestPermissionNow: Boolean = true) :
        ActivityCompat.OnRequestPermissionsResultCallback {

    @IntDef(
            REQUEST_CODE_CAMERA.toLong(),
            REQUEST_CODE_EXTERNAL_FILES.toLong()
    )
    annotation class Code

    companion object {

        @Code
        const val REQUEST_CODE_CAMERA = 102
        const val REQUEST_CODE_EXTERNAL_FILES = 203

        val delegate: ActivityCompat.PermissionCompatDelegate = LooprPermissionDelegate()
    }

    var isPermissionGranted: Boolean
        private set

    init {
        val code = ActivityCompat.checkSelfPermission(activity, permission)
        isPermissionGranted = code == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests permissions from the user for the permission put into this instance's constructor.
     * On success/failure, [onPermissionGranted] [onPermissionDenied] will be called automatically.
     */
    fun requestPermission() {
        if (isPermissionGranted) {
            onPermissionGranted.invoke()
        } else {
            requestPermissionWrapper()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val index = permissions.indexOf(permission)
        if (index == -1 || index >= grantResults.size || requestCode != this.requestCode) {
            return
        }

        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
            onPermissionGranted.invoke()
        } else {
            onPermissionDenied.invoke()
        }
    }

    /**
     * A wrapper around [ActivityCompat.requestPermissions] to make testing easier, since we can
     * stub this method and mock it. This method is set to *open* for testing purposes
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    open fun requestPermissionWrapper() {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    private class LooprPermissionDelegate : ActivityCompat.PermissionCompatDelegate {

        override fun requestPermissions(activity: Activity, permissions: Array<out String>, requestCode: Int): Boolean {
            // Allow the framework to handle the requesting of permissions
            return false
        }

        override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?): Boolean {
            return PermissionHandler.Companion::class.members
                    .filterIsInstance<KProperty<Int>>()
                    .filter { it.isConst }
                    .any { it.getter.call(PermissionHandler.Companion) == requestCode }
        }

    }

}