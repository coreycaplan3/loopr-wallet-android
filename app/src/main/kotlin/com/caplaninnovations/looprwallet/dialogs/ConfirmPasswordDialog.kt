package com.caplaninnovations.looprwallet.dialogs

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPassword
import com.caplaninnovations.looprwallet.utilities.loge
import kotlinx.android.synthetic.main.dialog_confirm_password.*
import org.web3j.crypto.WalletUtils
import java.io.File

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
        const val KEY_WALLET = "_WALLET"

        fun createInstance(walletCreationPassword: WalletCreationPassword): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply { putParcelable(KEY_WALLET, walletCreationPassword) }
            }
        }
    }

    private lateinit var walletCreationPassword: WalletCreationPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        walletCreationPassword = arguments?.getParcelable(KEY_WALLET) ?: throw IllegalStateException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirm_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            it.setOnShowListener {
                val bottomSheetDialog = dialog as? BottomSheetDialog
                val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            cancelButton.setOnClickListener { dismiss() }

            confirmButton.setOnClickListener {
                val generatedWalletName = WalletUtils.generateFullNewWalletFile(walletCreationPassword.password, it.context.filesDir)
                val createdFile = File(it.context.filesDir, generatedWalletName)
                val newFile = File(it.context.filesDir, walletCreationPassword.walletName + ".json")
                if (!createdFile.renameTo(newFile)) {
                    loge("Could not rename generated wallet file!", IllegalStateException())
                }
            }
        }

    }


}