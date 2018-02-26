package com.caplaninnovations.looprwallet.models.android.settings

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class ThemeSettingsTest : BaseDaggerTest() {

    @Inject
    lateinit var themeSettings: ThemeSettings

    @Before
    fun setup() {
        component.inject(this)
    }

    @Test
    fun getCurrentTheme() {
        val theme = themeSettings.getCurrentTheme()
        assertEquals(R.style.AppTheme_Dark, theme)
    }

    @Test
    fun saveTheme() {
        val theme = themeSettings.getCurrentTheme()
        assertEquals(R.style.AppTheme_Dark, theme)

        themeSettings.saveTheme(ThemeSettings.ThemeValues.KEY_LIGHT_THEME)
        assertEquals(R.style.AppTheme_Light, themeSettings.getCurrentTheme())
    }

}