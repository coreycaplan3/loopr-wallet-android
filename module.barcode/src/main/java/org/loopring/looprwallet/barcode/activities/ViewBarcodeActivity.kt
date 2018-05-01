package org.loopring.looprwallet.barcode.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.Toolbar
import androidx.view.doOnPreDraw
import kotlinx.android.synthetic.main.activity_view_barcode.*
import org.loopring.looprwallet.barcode.R
import org.loopring.looprwallet.barcode.utilities.BarcodeUtility
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.utilities.ViewUtility

/**
 * Created by Corey on 4/30/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewBarcodeActivity : BaseActivity() {

    companion object {

        private const val KEY_TITLE_TEXT = "_TITLE_TEXT"
        private const val KEY_BARCODE_TEXT = "_BARCODE_TEXT"

        fun route(activity: Activity, titleText: String, barcodeText: String) {
            val intent = Intent(activity, ViewBarcodeActivity::class.java)
                    .putExtra(KEY_TITLE_TEXT, titleText)
                    .putExtra(KEY_BARCODE_TEXT, barcodeText)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_view_barcode

    override val isSecureActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            title = intent.getStringExtra(KEY_TITLE_TEXT)
            setBackgroundColor(Color.BLACK)
            navigationIcon = ViewUtility.getNavigationIcon(R.drawable.ic_arrow_back_white_24dp, theme)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val bitmapText = intent.getStringExtra(KEY_BARCODE_TEXT)
        viewBarcodeImage.doOnPreDraw {
            viewBarcodeImage.setImageBitmap(BarcodeUtility.encodeTextToBitmap(bitmapText, viewBarcodeImage.width))
            viewBarcodeLabel.text = bitmapText
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}