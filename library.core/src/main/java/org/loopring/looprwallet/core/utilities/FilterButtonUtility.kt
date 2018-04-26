package org.loopring.looprwallet.core.utilities

import android.widget.Button
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
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
        button.background = drawable(R.drawable.button_background_material)
    }

    fun toNormal(button: Button) {
        val currentTheme = button.context.theme
        val textColor = currentTheme.getResourceIdFromAttrId(R.attr.borderlessButtonColor)
        button.setTextColor(col(textColor))
        button.background = drawable(R.drawable.ripple_effect_accent)
    }

}