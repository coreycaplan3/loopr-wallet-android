package org.loopring.looprwallet.core.models.settings

import android.support.test.runner.AndroidJUnit4
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
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
        testCoreLooprComponent.inject(this)
    }

    @Test
    fun getCurrentTheme() {
        val theme = themeSettings.getCurrentTheme()
        assertEquals(R.style.AppTheme_Dark, theme)
    }

}