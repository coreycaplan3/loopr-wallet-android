package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.test.InstrumentationRegistry
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class LooprThemeSettingsTest: BaseDaggerTest() {

    private lateinit var context: Context
    private lateinit var looprThemeSettings: LooprThemeSettings

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()

        looprThemeSettings = LooprThemeSettings(looprSettings)
    }

    @Test
    fun getCurrentTheme() {
        val theme = looprThemeSettings.getCurrentTheme()
        assertEquals(R.style.AppTheme_Light, theme)
    }

    @Test
    fun saveTheme() {
        val theme = looprThemeSettings.getCurrentTheme()
        assertEquals(R.style.AppTheme_Light, theme)

        looprThemeSettings.saveTheme(LooprThemeSettings.ThemeValues.KEY_DARK_THEME)

        assertEquals(R.style.AppTheme_Dark, looprThemeSettings.getCurrentTheme())
    }

    @After
    fun tearDown() {
        context.getSharedPreferences(LooprSettingsImpl.KEY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
    }

}