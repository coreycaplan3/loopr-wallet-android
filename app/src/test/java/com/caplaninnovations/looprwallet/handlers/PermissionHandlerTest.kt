package com.caplaninnovations.looprwallet.handlers

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.utilities.loge
import junit.framework.Assert.*
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.internal.verification.api.VerificationData
import org.mockito.verification.VerificationMode
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created by Corey on 2/9/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(PowerMockRunner::class)
class PermissionHandlerTest {

    companion object {
        /**
         * The permission is granted in the call to [ActivityCompat.checkSelfPermission]. This is
         * irrelevant to later calls to [BaseActivity.onRequestPermissionsResult]
         */
        const val PERMISSION_GRANTED_IMMEDIATELY = "camera_granted_immediately"

        /**
         * The permission is denied in the call to [ActivityCompat.checkSelfPermission]. This is
         * irrelevant to later calls to [BaseActivity.onRequestPermissionsResult]
         */
        const val PERMISSION_DENIED_IN_INITIAL_CHECK = "camera_denied_in_initial_check"

        /**
         * The permission is granted in the call to [BaseActivity.onRequestPermissionsResult]. This
         * is irrelevant to calls to [ActivityCompat.checkSelfPermission].
         */
        const val REQUEST_CODE_FOR_SUCCESS = 1

        /**
         * The permission is denied in the call to [BaseActivity.onRequestPermissionsResult]. This
         * is irrelevant to calls to [ActivityCompat.checkSelfPermission].
         */
        const val REQUEST_CODE_FOR_FAILURE = 2
    }

    @Mock
    private lateinit var activity: BaseActivity

    // Both of these success/failures are based on their request codes and not the permission being
    // granted immediately

    private lateinit var successPermissionHandler: PermissionHandler
    private lateinit var failurePermissionHandler: PermissionHandler

    private var isSuccessFunctionCalled = false

    private var isFailureFunctionCalled = false

    @PrepareForTest(ActivityCompat::class)
    @Before
    fun setup() {
        PowerMockito.mockStatic(ActivityCompat::class.java)

        Mockito.`when`(ActivityCompat.checkSelfPermission(activity, PERMISSION_DENIED_IN_INITIAL_CHECK))
                .thenReturn(PackageManager.PERMISSION_DENIED)
        assertEquals(PackageManager.PERMISSION_DENIED, ActivityCompat.checkSelfPermission(activity, PERMISSION_DENIED_IN_INITIAL_CHECK))

        Mockito.`when`(ActivityCompat.checkSelfPermission(activity, PERMISSION_GRANTED_IMMEDIATELY))
                .thenReturn(PackageManager.PERMISSION_GRANTED)
        assertEquals(PackageManager.PERMISSION_GRANTED, ActivityCompat.checkSelfPermission(activity, PERMISSION_GRANTED_IMMEDIATELY))


        successPermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        )

        failurePermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        )

        Mockito.`when`(ActivityCompat.requestPermissions(activity, arrayOf(PERMISSION_DENIED_IN_INITIAL_CHECK), REQUEST_CODE_FOR_SUCCESS))
                .then { invocation ->
                    loge("Invoking mocked ActivityCompat.requestPermissions!")
                    when {
                        invocation.arguments.contains(REQUEST_CODE_FOR_SUCCESS) -> thenPermissionGranted()
                        invocation.arguments.contains(REQUEST_CODE_FOR_FAILURE) -> thenPermissionDenied()
                    }
                }

        PowerMockito.verifyStatic()

        // Neither of these values should have been set, since we ARE delaying requesting permissions
        assertFalse(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
    }

    @Test
    fun requestPermission() {
        successPermissionHandler.requestPermission()
        assertTrue(isSuccessFunctionCalled)

        // Reset the value back to false
        isSuccessFunctionCalled = false

        failurePermissionHandler.requestPermission()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)
    }

    @Test
    fun onRequestPermissionsResult() {
        // We are going to grant the permissionHandler the permission
        thenPermissionGranted()
        assertTrue(isSuccessFunctionCalled)

        // Reset the value back to false
        isSuccessFunctionCalled = false

        // We are going to deny the permissionHandler the permission
        thenPermissionDenied()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)
    }

    @Test
    fun permissionIsGrantedImmediately() {
        successPermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_GRANTED_IMMEDIATELY,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        )

        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        failurePermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_GRANTED_IMMEDIATELY,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        )

        // Requesting the permission fails, but checking the permission returns GRANTED --> so,
        // failure from [requestPermission] is over-ruled!
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
    }

    @Test
    fun turnImmediateRequestingPermissionOn() {
        successPermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        )

        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        failurePermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        )

        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)
    }

    private fun thenPermissionGranted() {
        successPermissionHandler.onRequestPermissionsResult(
                REQUEST_CODE_FOR_SUCCESS,
                arrayOf(PERMISSION_DENIED_IN_INITIAL_CHECK),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
    }

    private fun thenPermissionDenied() {
        successPermissionHandler.onRequestPermissionsResult(
                REQUEST_CODE_FOR_SUCCESS,
                arrayOf(PERMISSION_DENIED_IN_INITIAL_CHECK),
                intArrayOf(PackageManager.PERMISSION_DENIED)
        )
    }

    private fun successFunction() {
        isSuccessFunctionCalled = true
    }

    private fun failureFunction() {
        isFailureFunctionCalled = true
    }

}