package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.caplaninnovations.looprwallet.R

class SecurityActivity : BaseActivity() {

    companion object {
        private const val KEY_IS_DIALOG_SHOWING = "_IS_DIALOG_SHOWING"
    }

    override val contentView: Int
        get() = R.layout.activity_security

    override val isSecurityActivity: Boolean
        get() = false

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setMessage(R.string.exit_app_rationale)
                .setPositiveButton(R.string.exit, { dialog, _ ->
                    dialog.dismiss()
                    startActivity(MainActivity.createIntentToFinishApp())
                })
                .setNegativeButton(android.R.string.cancel, { dialog, _ ->
                    dialog.dismiss()
                })
                .create()

        if (savedInstanceState?.getBoolean(KEY_IS_DIALOG_SHOWING) == true) {
            dialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        dialog.show()
    }

    /**
     * The password was entered correctly, so the user may return to what he/she was doing
     */
    fun onCorrectPassword() {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(KEY_IS_DIALOG_SHOWING, dialog.isShowing)
    }

    override fun onDestroy() {
        super.onDestroy()

        dialog.dismiss()
    }

}
