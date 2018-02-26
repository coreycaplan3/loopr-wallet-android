package com.caplaninnovations.looprwallet.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.annotation.RequiresPermission
import android.support.annotation.StringRes
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import java.io.File
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Created by Corey on 2/21/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object FilesUtility {

    @Throws(ActivityNotFoundException::class)
    fun getFileChooserIntent(@StringRes title: Int): Intent {
        return if (isKitkat()) {
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*")
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*")
            Intent.createChooser(intent, str(title))
        }
    }

    fun getFileFromUri(context: Context, uri: Uri?): File? {
        return uri?.let { File(getPath(context, it)) }
    }

    fun getKeystoreFileName(walletName: String): String = walletName + ".json"

    fun getDownloadsDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    /**
     * Saves the given file in the *Download*s folder.
     */
    @Throws(Exception::class)
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveFileToDownloadFolder(fileToSave: File) {
        val allBytes = fileToSave.readBytes()

        val service = (LooprWalletApp.getContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
        service.addCompletedDownload(
                fileToSave.name,
                String.format(str(R.string.description_keystore_download), fileToSave.name),
                true,
                "application/json",
                fileToSave.path,
                allBytes.size.toLong(),
                true)
    }

    // MARK - Private Methods

    @SuppressLint("NewApi")
    private fun getPath(context: Context, uri: Uri): String? {
        when {
            isKitkat() && DocumentsContract.isDocumentUri(context, uri) -> {
                return getPathFromKitKat(context, uri)
            }
            "content" == uri.scheme?.toLowerCase() -> {
                return if (isGooglePhotosUri(uri))
                    uri.lastPathSegment
                else
                    getDataColumn(context, uri, null, null)
            }
            "file" == uri.scheme?.toLowerCase() -> {
                return uri.path
            }
            else -> {
                loge("Invalid scheme, found ${uri.scheme}!", IllegalArgumentException())
                return null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getPathFromKitKat(context: Context, uri: Uri): String? {
        return when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split.firstOrNull()

                if ("primary" == type?.toLowerCase()) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    loge("Non-primary path was not retrievable...", IllegalStateException())
                    null
                }
            }

            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
                return getDataColumn(context, contentUri, null, null)
            }

            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                val contentUri: Uri = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> {
                        loge("Invalid type, found $type!", IllegalArgumentException())
                        return null
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }

            else -> null
        }
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        val column = "_data"
        val projection = arrayOf(column)
        return context.contentResolver.query(uri, projection, selection, selectionArgs, null).use {
            if (it != null && it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(column)
                it.getString(index)
            } else {
                null
            }
        }
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}