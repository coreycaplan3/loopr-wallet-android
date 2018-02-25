package com.caplaninnovations.looprwallet.utilities

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 2/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To standardize the creation and usage of [Dialog]s.
 *
 */
object DialogUtility {

    fun createFilePermissionsDialog(context: Context, onPositiveButtonClick: () -> Unit): AlertDialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.file_permissions_needed)
                .setMessage(R.string.rationale_file_permission)
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .setPositiveButton(R.string.request_permission) { dialog, _ ->
                    dialog.dismiss()
                    onPositiveButtonClick.invoke()
                }
                .create()
    }

}