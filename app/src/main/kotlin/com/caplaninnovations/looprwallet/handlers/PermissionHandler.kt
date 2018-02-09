package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.IntDef
import android.support.v4.app.ActivityCompat
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.utilities.loge
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType

/**
 * Created by Corey Caplan on 2/9/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class PermissionHandler(private val activity: BaseActivity,
                        private val permission: String,
                        @Code private val requestCode: Int) :
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