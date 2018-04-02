package org.loopring.looprwallet.core.utilities

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.handlers.PermissionHandler

/**
 * Created by Corey Caplan on 2/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To standardize the creation and usage of [Dialog]s.
 *
 */
object DialogUtility {

    /**
     * @param permissionHandler A [PermissionHandler] used to invoke
     * [PermissionHandler.requestPermission] when the user clicks the positive button
     */
    fun createFilePermissionsDialog(context: Context, permissionHandler: PermissionHandler): AlertDialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.file_permissions_needed)
                .setMessage(R.string.rationale_file_permission)
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .setPositiveButton(R.string.request_permission) { dialog, _ ->
                    dialog.dismiss()
                    permissionHandler.requestPermission()
                }
                .create()
    }

}