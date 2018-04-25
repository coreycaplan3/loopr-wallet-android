package org.loopring.looprwallet.barcode.utilities

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException


/**
 *  Created by Corey on 4/17/2018.
 *
 *  Project: loopr-android-official
 *
 *  Purpose of Class:
 *
 */
object BarcodeUtility {

    @Throws(WriterException::class)
    fun encodeTextToBitmap(value: String, dimension: Int): Bitmap? {

        val bitMatrix = try {
            MultiFormatWriter().encode(value, BarcodeFormat.DATA_MATRIX, dimension, dimension, null)
        } catch (ignored: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth)
                pixels[offset + x] = when {
                    bitMatrix.get(x, y) -> Color.BLACK
                    else -> Color.WHITE
                }
        }
        return Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444).apply {
            setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)
        }
    }

}