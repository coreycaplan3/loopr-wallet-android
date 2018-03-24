package com.caplaninnovations.looprwallet.models.android.settings

import android.support.annotation.StyleRes
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.loge
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility

/**
 * Created by Corey Caplan on 1/30/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ThemeSettings(private val looprSettings: LooprSettings) {

    companion object {
        val KEY_THEME = ApplicationUtility.str(R.string.settings_theme_key)

        private val DARK_THEME = ApplicationUtility.str(R.string.settings_theme_dark)
        private val LIGHT_THEME = ApplicationUtility.str(R.string.settings_theme_light)
    }

    /**
     * The theme for the application
     */
    @StyleRes
    fun getCurrentTheme(): Int {
        val theme = looprSettings.getString(KEY_THEME) ?: DARK_THEME
        return getThemeFromSettings(theme)
    }

    @StyleRes
    fun getThemeFromSettings(themeValue: String): Int = when (themeValue) {
        DARK_THEME -> R.style.AppTheme_Dark
        LIGHT_THEME -> R.style.AppTheme_Light
        else -> {
            loge("Invalid theme, found: $themeValue", IllegalArgumentException())
            R.style.AppTheme_Dark
        }
    }

}