package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import com.caplaninnovations.looprwallet.validators.BaseValidator
import kotlinx.android.synthetic.main.fragment_restore_keystore.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import java.io.File
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 2/25/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class RestoreWalletKeystoreFragmentTest : BaseDaggerFragmentTest<RestoreWalletKeystoreFragment>() {

    @Rule
    @JvmField
    val grantFilePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override val fragment = RestoreWalletKeystoreFragment()
    override val tag = RestoreWalletKeystoreFragment.TAG

    private val goodName = "loopr-wallet-restore-keystore"
    private val badName = "loopr-wallet$" // cannot contain special characters

    private val goodPassword = "looprwallet" // this is the actual password used to unlock the keystore
    private val badPassword = "loopr" // too short

    private val emptyString = ""
    private val nullString: String? = null

    private val fileName = "loopr-test-keystore.json"

    @Test
    fun initialState_buttonDisabled_textEmpty() {
        Espresso.onView(`is`(fragment.keystoreUnlockButton))
                .check(CustomViewAssertions.isDisabled())

        Espresso.onView(`is`(fragment.keystorePasswordEditText))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(withText(emptyString)))
    }

    @Test
    fun nameOkay_passwordEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        // Still empty, so error should be empty
        Espresso.onView(`is`(fragment.keystorePasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.keystoreUnlockButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameBad_passwordBad() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        assertNotNull(fragment.walletNameInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletNameInputLayout.error)

        Espresso.onView(`is`(fragment.keystorePasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        assertNotNull(fragment.keystorePasswordInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.keystorePasswordInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.keystoreUnlockButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay_fileOkay() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.keystorePasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        val task = FutureTask { fragment.keystoreUri = Uri.fromFile(getKeystoreFile()) }
        waitForTask(activityRule.activity, task, true)

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.keystoreUnlockButton))
                .check(matches(isEnabled()))
                .perform(click())

        assertActivityActive(MainActivity::class.java, 7000)
    }

    @Test
    fun rotation_fileShouldBePersisted() {
        val file = getKeystoreFile()

        val task = FutureTask { fragment.keystoreUri = Uri.fromFile(file) }
        waitForTask(activityRule.activity, task, false)

        Espresso.onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())

        val recreatedFragment = activityRule.activity
                .supportFragmentManager
                .findFragmentByTag(RestoreWalletKeystoreFragment.TAG) as RestoreWalletKeystoreFragment

        assertNotNull(recreatedFragment.keystoreFile)
        assertEquals(file.name, recreatedFragment.keystoreFile!!.name)
    }

    @Test
    fun rotation_dialogShouldShow() {
        val task = FutureTask { fragment.filePermissionsDialog?.show() }
        activityRule.activity.runOnUiThread(task)
        task.get()

        Espresso.onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())

        val recreatedFragment = activityRule.activity
                .supportFragmentManager
                .findFragmentByTag(RestoreWalletKeystoreFragment.TAG) as RestoreWalletKeystoreFragment

        assertTrue(recreatedFragment.filePermissionsDialog?.isShowing == true)
    }

    @Test
    fun fileSelected_onActivityResult() {
        val requestCode = RestoreWalletKeystoreFragment.KEY_OPEN_FILE_REQUEST_CODE
        val resultCode = Activity.RESULT_OK

        val file = getKeystoreFile()
        val intent = Intent().setData(Uri.fromFile(file))

        val task = FutureTask { fragment.onActivityResult(requestCode, resultCode, intent) }
        waitForTask(activityRule.activity, task, false)

        assertNotNull(fragment.keystoreUri)
        assertEquals(file.name, fragment.keystoreFile?.name)
    }

    // Mark - Private Methods

    private fun getKeystoreFile(): File {
        val assetManager = activityRule.activity.assets
        // This is the "real" file's name. Do NOT change it.
        val content = assetManager.open("loopr-wallet.json").use { it.reader().readText() }

        val file = File(activityRule.activity.cacheDir, fileName)
        file.writeBytes(content.toByteArray())

        FilesUtility.saveFileToDownloadFolder(file)

        return file
    }

}