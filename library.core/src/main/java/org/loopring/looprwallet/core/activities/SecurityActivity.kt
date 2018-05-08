package org.loopring.looprwallet.core.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import org.loopring.looprwallet.core.R

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class SecurityActivity : BaseActivity() {

    companion object {
        private const val KEY_IS_DIALOG_SHOWING = "_IS_DIALOG_SHOWING"
    }

    override val contentViewRes: Int
        get() = R.layout.activity_security

    override val isSignInRequired: Boolean
        get() = false

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setMessage(R.string.exit_app_rationale)
                .setPositiveButton(R.string.exit, { dialog, _ ->
                    dialog.dismiss()
                    startActivity(SettingsActivity.createIntentToFinishApp())
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
