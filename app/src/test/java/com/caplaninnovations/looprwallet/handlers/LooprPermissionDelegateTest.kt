package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito

/**
 * Created by Corey on 2/9/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprPermissionDelegateTest {

    private val delegate = PermissionHandler.delegate

    private val activity = Mockito.mock(Activity::class.java)

    @Test
    fun requestPermissions() {
        assertFalse(delegate.requestPermissions(activity, arrayOf(), 123))
    }

    @Test
    fun onActivityResult() {
        val requestCamera = delegate.onActivityResult(activity, PermissionHandler.REQUEST_CODE_CAMERA, 1, null)
        assertTrue(requestCamera)

        val requestNothing = delegate.onActivityResult(activity, 0, 1, null)
        assertFalse(requestNothing)

        val requestCameraAsRawNumber = delegate.onActivityResult(activity, 10320, 123, null)
        assertTrue(requestCameraAsRawNumber)
    }

}