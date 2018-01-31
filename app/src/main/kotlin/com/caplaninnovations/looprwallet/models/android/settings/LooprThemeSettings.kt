package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.annotation.StringDef
import android.support.annotation.StyleRes
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 1/30/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprThemeSettings(context: Context) : LooprSettingsManager(context) {

    @StringDef(KEY_LIGHT_THEME, KEY_DARK_THEME)
    annotation class Name

    companion object {
        const val KEY_LIGHT_THEME = "_LIGHT_THEME"
        const val KEY_DARK_THEME = "_DARK_THEME"
    }

    /**
     * The theme for the application
     */
    @StyleRes
    fun getCurrentTheme(): Int {
        val theme = getString(LooprSettingsManager.KEY_THEME) ?: KEY_LIGHT_THEME
        return when (theme) {
            KEY_LIGHT_THEME -> R.style.AppTheme_Light
            KEY_DARK_THEME -> R.style.AppTheme_Dark
            else -> throw IllegalStateException("Invalid theme, found: $theme")
        }
    }

    /**
     *
     */
    fun saveTheme(@Name theme: String) {
        putString(KEY_THEME, theme)
    }

}