package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_restore_keystore.*
import android.support.design.widget.Snackbar
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.longToast
import com.caplaninnovations.looprwallet.utilities.snackbar
import java.io.File


/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class RestoreWalletKeystoreFragment : BaseFragment() {

    companion object {
        val TAG: String = RestoreWalletKeystoreFragment::class.java.simpleName

        const val KEY_OPEN_FILE_REQUEST_CODE = 1943934
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_keystore

    var keystoreFile: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        keystoreSelectFileButton.setOnClickListener {
            try {
                val intent = FilesUtility.getFileChooserIntent(R.string.title_select_keystore_file)
                startActivityForResult(intent, KEY_OPEN_FILE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                it.snackbar(R.string.error_install_file_manager, Snackbar.LENGTH_INDEFINITE)
            }
        }

        keystoreUnlockButton.setOnClickListener {
            //            WalletUtils.loadCredentials()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = this.context ?: return

        if (requestCode == KEY_OPEN_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                loge("Invalid state, how did this happen?", IllegalStateException())
                context.longToast(R.string.error_retrieve_file)
                return
            }

            keystoreFile = FilesUtility.getFileFromActivityResult(context, data)
        }
    }

}