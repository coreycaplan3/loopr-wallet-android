package com.caplaninnovations.looprwallet.utilities

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.support.annotation.RequiresPermission
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import java.io.File

/**
 * Created by Corey on 2/21/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
object FilesUtility {

    fun getDownloadsDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * Saves the given file in the *Download*s folder.
     */
    @Throws(Exception::class)
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveFileToDownloadFolder(fileToSave: File) {
        val downloadedFile = File(getDownloadsDirectory(), fileToSave.name)
        val allBytes = fileToSave.readBytes()
        downloadedFile.writeBytes(allBytes)

        val service = (LooprWalletApp.getContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
        service.addCompletedDownload(
                downloadedFile.name,
                String.format(str(R.string.description_keystore_download), downloadedFile.name),
                true,
                "application/json",
                downloadedFile.path,
                allBytes.size.toLong(),
                true)
    }

}