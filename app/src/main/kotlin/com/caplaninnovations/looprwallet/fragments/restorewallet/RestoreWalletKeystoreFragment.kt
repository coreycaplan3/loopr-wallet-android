package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_restore_keystore.*
import android.net.Uri
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.longToast
import org.web3j.crypto.WalletUtils
import java.io.File


/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class RestoreWalletKeystoreFragment: BaseFragment() {

    companion object {
        const val KEY_OPEN_FILE_REQUEST_CODE = 1943934
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_keystore

    var keystoreFile: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        keystoreSelectFileButton.setOnClickListener {
            // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
            // browser.
            val intent = FilesUtility.getFileChooserIntent()
            startActivityForResult(intent, KEY_OPEN_FILE_REQUEST_CODE)
        }

        keystoreUnlockButton.setOnClickListener {
//            WalletUtils.loadCredentials()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == KEY_OPEN_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data == null) {
                loge("Invalid state, how did this happen?", IllegalStateException())
                context?.longToast(R.string.error_retrieve_file)
                return
            }

            keystoreFile = FilesUtility.getFileFromActivityResult(data)
        }
    }

}