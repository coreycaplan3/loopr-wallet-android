package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.R
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class LooprThemeSettingsTest {

    private lateinit var context: Context
    private lateinit var looprThemeSettings: LooprThemeSettings

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()

        looprThemeSettings = LooprThemeSettings(context)
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

        looprThemeSettings.saveTheme(LooprThemeSettings.KEY_DARK_THEME)

        assertEquals(R.style.AppTheme_Dark, looprThemeSettings.getCurrentTheme())
    }

    @After
    fun tearDown() {
        context.getSharedPreferences(LooprSettingsManager.KEY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
    }

}