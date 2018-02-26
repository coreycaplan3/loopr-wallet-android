package com.caplaninnovations.looprwallet.utilities

import android.Manifest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import org.junit.After

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.io.File

/**
 * Created by Corey on 2/21/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class FilesUtilityTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    @get:Rule
    val grantFilePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val baseFileName = "test-file.txt"

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
        activityRule.activity.filesDir.let {
            File(it, baseFileName).apply { if (exists()) delete() }
        }
    }

    @Test
    fun getDownloadsDirectory() {
        val downloadsDirectory = FilesUtility.getDownloadsDirectory()

        assertTrue(downloadsDirectory.isDirectory)
        assertTrue(downloadsDirectory.name.contains("download", true))
    }

    @Test
    fun saveFileToDownloadFolder() {
        val testFile = File(activityRule.activity.cacheDir, baseFileName)
        if (!testFile.exists() && !testFile.createNewFile()) {
            throw IllegalStateException("Could not create test file!")
        }

        testFile.writeBytes("Hello World!".toByteArray())

        activityRule.activity.runOnUiThread {
            FilesUtility.saveFileToDownloadFolder(testFile)
        }
    }

}