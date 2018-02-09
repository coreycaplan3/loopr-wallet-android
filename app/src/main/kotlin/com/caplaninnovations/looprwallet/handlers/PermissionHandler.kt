package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.caplaninnovations.looprwallet.utilities.loge

/**
 * Created by Corey Caplan on 2/9/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class PermissionHandler(private val activity: Activity, private val permission: String) :
        ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {

        const val REQUEST_CODE = 10320

        val delegate: ActivityCompat.PermissionCompatDelegate = LooprPermissionDelegate()

    }

    private val isPermissionGranted: Boolean

    init {
        val code = ActivityCompat.checkSelfPermission(activity, permission)
        isPermissionGranted = code == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermission()
        }
    }

    fun requestPermission() {

    }

    fun runIfPermissionActive(f: () -> Unit) {
        if (isPermissionGranted) {
            f.invoke()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        loge("onRequestPermissionsResult")
    }

    private class LooprPermissionDelegate() : ActivityCompat.PermissionCompatDelegate {

        override fun requestPermissions(activity: Activity, permissions: Array<out String>, requestCode: Int): Boolean {
            // Allow the framework to handle the requesting of permissions
            return false
        }

        override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?): Boolean {
            return requestCode == REQUEST_CODE
        }

    }

}