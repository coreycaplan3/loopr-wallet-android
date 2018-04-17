package org.loopring.looprwallet.core.utilities

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import org.loopring.looprwallet.core.application.CoreLooprWalletApp


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
    private fun encodeTextToBitmap(value: String, dimension: Int): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter()
                    .encode(value, BarcodeFormat.DATA_MATRIX, dimension, dimension, null)

        } catch (ignored: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) pixels[offset + x] = when {
                bitMatrix.get(x, y) -> Color.BLACK
                else -> Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

}