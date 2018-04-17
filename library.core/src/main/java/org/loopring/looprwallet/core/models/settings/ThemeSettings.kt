package org.loopring.looprwallet.core.models.settings

import android.support.annotation.StyleRes
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.utilities.ApplicationUtility

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

        val DARK_THEME = ApplicationUtility.str(R.string.settings_theme_dark)
        val LIGHT_THEME = ApplicationUtility.str(R.string.settings_theme_light)
    }

    /**
     * The theme for the application
     */
    @StyleRes
    fun getCurrentTheme(): Int {
        return getThemeFromSettings(getCurrentThemeForSettings())
    }

    /**
     * @return The value of the current theme, as seen by the Android settings framework
     */
    fun getCurrentThemeForSettings(): String {
        return looprSettings.getString(KEY_THEME) ?: DARK_THEME
    }

    // MARK - Private Methods

    /**
     * Gets the [StyleRes] theme from settings, based on the string (as it's stored in Android's
     * settings).
     */
    @StyleRes
    private fun getThemeFromSettings(themeValue: String): Int = when (themeValue) {
        DARK_THEME -> R.style.AppTheme_Dark
        LIGHT_THEME -> R.style.AppTheme_Light
        else -> {
            loge("Invalid theme, found: $themeValue", IllegalArgumentException())
            R.style.AppTheme_Dark
        }
    }

}