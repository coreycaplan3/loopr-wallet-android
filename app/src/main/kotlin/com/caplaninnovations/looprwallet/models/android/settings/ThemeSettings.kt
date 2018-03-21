package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.annotation.StringDef
import android.support.annotation.StyleRes
import com.caplaninnovations.looprwallet.R
import javax.inject.Inject

/**
 * Created by Corey Caplan on 1/30/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ThemeSettings(private val looprSettings: LooprSettings) {

    @StringDef(ThemeValues.KEY_LIGHT_THEME, ThemeValues.KEY_DARK_THEME)
    annotation class Name

    companion object {
        const val KEY_THEME = "_THEME"
    }

    object ThemeValues {
        const val KEY_LIGHT_THEME = "_LIGHT_THEME"
        const val KEY_DARK_THEME = "_DARK_THEME"
    }


    /**
     * The theme for the application
     */
    @StyleRes
    fun getCurrentTheme(): Int {
        val theme = looprSettings.getString(KEY_THEME) ?: ThemeValues.KEY_DARK_THEME
        return when (theme) {
            ThemeValues.KEY_LIGHT_THEME -> R.style.AppTheme_Light
            ThemeValues.KEY_DARK_THEME -> R.style.AppTheme_Dark
            else -> throw IllegalStateException("Invalid theme, found: $theme")
        }
    }

    /**
     * Saves the specified theme to be the current one
     */
    fun saveTheme(@Name theme: String) {
        looprSettings.putString(KEY_THEME, theme)
    }

}