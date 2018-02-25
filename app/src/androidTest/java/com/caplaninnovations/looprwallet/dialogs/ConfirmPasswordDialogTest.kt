package com.caplaninnovations.looprwallet.dialogs

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPhrase
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 2/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(AndroidJUnit4::class)
class ConfirmPasswordDialogTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private val walletName = "loopr-wallet"
    private val password = "looprloopr"

    private val keystoreDialog = ConfirmPasswordDialog.createInstance(
            WalletCreationKeystore(walletName, password)
    )

    private val phraseDialog = ConfirmPasswordDialog.createInstance(
            WalletCreationPhrase(walletName, password, listOf())
    )

    @Before
    fun setUp() {
    }

}