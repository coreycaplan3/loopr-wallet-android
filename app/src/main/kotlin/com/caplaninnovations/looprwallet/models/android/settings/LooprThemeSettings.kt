package com.caplaninnovations.looprwallet.models.android.settings

import android.support.annotation.StringDef

/**
 * Created by Corey Caplan on 1/30/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
object LooprTheme {

    @StringDef(LooprTheme.lightTheme, LooprTheme.darkTheme)
    annotation class Name

    const val lightTheme = "_lightTheme"
    const val darkTheme = "_darkTheme"

}