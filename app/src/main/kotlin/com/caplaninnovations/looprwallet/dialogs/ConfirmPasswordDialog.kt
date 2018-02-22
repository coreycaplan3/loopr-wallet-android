package com.caplaninnovations.looprwallet.dialogs

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPhrase
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

        fun createInstance(walletCreationKeystore: WalletCreationKeystore): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply { putParcelable(KEY_WALLET, walletCreationKeystore) }
            }
        }

        fun createInstance(walletCreationPhrase: WalletCreationPhrase): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply { putParcelable(KEY_WALLET, walletCreationPhrase) }
            }
        }
    }

    private val walletCreationKeystore: WalletCreationKeystore? by lazy {
        arguments?.getParcelable(KEY_WALLET) as? WalletCreationKeystore
    }

    private val walletCreationPhrase: WalletCreationPhrase? by lazy {
        arguments?.getParcelable(KEY_WALLET) as? WalletCreationPhrase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val walletList = listOf(
                walletCreationKeystore,
                walletCreationPhrase
        )
        if (walletList.filter { it != null }.size != 1) {
            throw IllegalStateException("Invalid argument passed!")
        }
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
        }

        cancelButton.setOnClickListener { dismiss() }

        walletCreationKeystore?.let { wallet ->
            confirmButton.setOnClickListener { clickListenerForKeystore(it.context, wallet) }
        }

        walletCreationPhrase?.let { wallet ->
            confirmButton.setOnClickListener { clickListenerForPhrase(it.context, wallet) }
        }

        confirmButton.setOnClickListener {
        }
    }

    private fun clickListenerForKeystore(context: Context, walletCreationKeystore: WalletCreationKeystore) {
        val generatedWalletName = WalletUtils.generateFullNewWalletFile(walletCreationKeystore.password, context.filesDir)
        val createdFile = File(context.filesDir, generatedWalletName)
        val newFile = File(context.filesDir, walletCreationKeystore.walletName + ".keystore")
        if (!createdFile.renameTo(newFile)) {
            loge("Could not rename generated wallet file!", IllegalStateException())
        }
    }

    private fun clickListenerForPhrase(context: Context, wallet: WalletCreationPhrase) {
        //TODO
    }

}