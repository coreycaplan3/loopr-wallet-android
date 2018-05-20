package org.loopring.looprwallet.core.utilities

import android.support.v4.view.ViewCompat
import android.widget.Button
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getAttrColorStateList
import org.loopring.looprwallet.core.extensions.getColorStateList
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.isLollipop
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable

/**
 *  Created by Corey on 4/25/2018.
 *
 *  Project: loopr-android
 *
 *  Purpose of Class:
 *
 */
object FilterButtonUtility {

    fun toSelected(button: Button) {
        val currentTheme = button.context.theme
        val textColor = currentTheme.getResourceIdFromAttrId(android.R.attr.textColorPrimaryInverseDisableOnly)
        button.setTextColor(col(textColor))

        if(!isLollipop()) {
            // If not using Lollipop, we need to apply tinting manually :(
            ViewCompat.setBackgroundTintList(button, currentTheme.getColorStateList(R.color.button_background_tint))
        }

        button.background = drawable(R.drawable.button_background_material, button.context)
    }

    fun toNormal(button: Button) {
        val currentTheme = button.context.theme
        val textColor = currentTheme.getResourceIdFromAttrId(R.attr.borderlessButtonColor)
        button.setTextColor(col(textColor))
        ViewCompat.setBackgroundTintList(button, currentTheme.getColorStateList(R.color.button_background_tint))
        button.background = drawable(R.drawable.ripple_effect_accent, button.context)
    }

}