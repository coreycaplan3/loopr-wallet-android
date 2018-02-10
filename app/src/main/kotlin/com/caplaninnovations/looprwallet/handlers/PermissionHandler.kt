package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.IntDef
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
class PermissionHandler(private val activity: BaseActivity,
                        private val permission: String,
                        @Code private val requestCode: Int,
                        private val onPermissionGranted: () -> Unit,
                        private val onPermissionDenied: () -> Unit,
                        shouldRequestPermissionNow: Boolean = true) :
        ActivityCompat.OnRequestPermissionsResultCallback {

    @IntDef(REQUEST_CODE_CAMERA.toLong())
    annotation class Code

    companion object {

        @Code
        const val REQUEST_CODE_CAMERA = 10320

        val delegate: ActivityCompat.PermissionCompatDelegate = LooprPermissionDelegate()
    }

    private val isPermissionGranted: Boolean

    init {
        val code = ActivityCompat.checkSelfPermission(activity, permission)
        isPermissionGranted = code == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted && shouldRequestPermissionNow) {
            // The permission wasn't granted and we aren't delaying requesting the transition, so
            // let's request it immediately.
            requestPermission()
        } else if (isPermissionGranted) {
            // The permission was granted, so let's notify the listener immediately
            onPermissionGranted.invoke()
        }
    }

    /**
     * Requests permissions from the user for the permission put into this instance's constructor.
     * On success/failure, [onPermissionGranted] [onPermissionDenied] will be called automatically.
     */
    fun requestPermission() {
        if (isPermissionGranted) {
            onPermissionGranted.invoke()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val index = permissions.indexOf(permission)
        if (index == -1 || index >= grantResults.size || requestCode != this.requestCode) {
            return
        }

        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted.invoke()
        } else {
            onPermissionDenied.invoke()
        }
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