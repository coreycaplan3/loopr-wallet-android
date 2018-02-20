package com.caplaninnovations.looprwallet.dialogs

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ConfirmPasswordDialog : BottomSheetDialogFragment() {

    companion object {

        val TAG: String = ConfirmPasswordDialog::class.java.simpleName
        const val KEY_PASSWORD = "_PASSWORD"

        fun createInstance(password: String): ConfirmPasswordDialog {
            val dialog = ConfirmPasswordDialog()

            val bundle = Bundle()
            bundle.putString(KEY_PASSWORD, password)
            dialog.arguments = bundle

            return dialog
        }
    }

    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        password = arguments?.getString(KEY_PASSWORD) ?: throw IllegalStateException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirm_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}