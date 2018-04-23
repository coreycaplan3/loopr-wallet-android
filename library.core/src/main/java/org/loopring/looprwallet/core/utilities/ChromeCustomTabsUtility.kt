package org.loopring.looprwallet.core.utilities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.customtabs.CustomTabsIntent
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str


/**
 *  Created by Corey on 4/23/2018.
 *
 *  Project: loopr-android
 *
 *  Purpose of Class:
 *
 */
object ChromeCustomTabsUtility {

    private const val REQUEST_CODE = 419

    fun getInstance(context: Context): CustomTabsIntent {
        val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
        val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_share_white_24dp)

        return CustomTabsIntent.Builder()
                .setToolbarColor(col(context.theme.getResourceIdFromAttrId(R.attr.colorPrimary)))
                .setShowTitle(true)
                .setActionButton(bitmap, str(R.string.content_description_share_chrome_tab), pendingIntent, true)
                .build()
    }

}