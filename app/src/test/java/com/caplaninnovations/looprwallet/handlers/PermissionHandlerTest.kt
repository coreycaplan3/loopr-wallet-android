package com.caplaninnovations.looprwallet.handlers

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import org.loopring.looprwallet.core.activities.BaseActivity
import junit.framework.Assert.*
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.handlers.PermissionHandler
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey on 2/9/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(MockitoJUnitRunner::class)
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

    @Before
    fun setup() {
        Mockito.`when`(ActivityCompat.checkSelfPermission(activity, PERMISSION_DENIED_IN_INITIAL_CHECK))
                .thenReturn(PackageManager.PERMISSION_DENIED)
        assertEquals(PackageManager.PERMISSION_DENIED, ActivityCompat.checkSelfPermission(activity, PERMISSION_DENIED_IN_INITIAL_CHECK))

        Mockito.`when`(ActivityCompat.checkSelfPermission(activity, PERMISSION_GRANTED_IMMEDIATELY))
                .thenReturn(PackageManager.PERMISSION_GRANTED)
        assertEquals(PackageManager.PERMISSION_GRANTED, ActivityCompat.checkSelfPermission(activity, PERMISSION_GRANTED_IMMEDIATELY))


        successPermissionHandler = Mockito.spy(PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        ))
        assertFalse(successPermissionHandler.shouldRequestPermissionNow)

        failurePermissionHandler = Mockito.spy(PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        ))
        assertFalse(failurePermissionHandler.shouldRequestPermissionNow)

        setupMockingRequestPermissions()
    }

    @Test
    fun requestPermission() {
        successPermissionHandler.requestPermission()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isSuccessFunctionCalled)

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
                shouldRequestPermissionNow = false
        )

        successPermissionHandler.requestPermission()

        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        failurePermissionHandler = PermissionHandler(
                activity = activity,
                permission = PERMISSION_GRANTED_IMMEDIATELY,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        )

        failurePermissionHandler.requestPermission()

        // Requesting the permission fails, but checking the permission returns GRANTED --> so,
        // failure from [requestPermission] is over-ruled!
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
    }

    @Test
    fun turnImmediateRequestingPermissionOn() {
        successPermissionHandler = Mockito.spy(PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        ))

        failurePermissionHandler = Mockito.spy(PermissionHandler(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        ))

        setupMockingRequestPermissions()

        assertTrue(successPermissionHandler.shouldRequestPermissionNow)

        successPermissionHandler.requestPermission()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        assertTrue(failurePermissionHandler.shouldRequestPermissionNow)
        failurePermissionHandler.requestPermission()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)
    }

    // MARK - Private Methods

    private fun setupMockingRequestPermissions() {
        Mockito.`when`(successPermissionHandler.requestPermissionWrapper())
                .thenAnswer({ thenPermissionGranted() })

        successPermissionHandler.requestPermissionWrapper()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)

        isSuccessFunctionCalled = false // Reset the value

        Mockito.`when`(failurePermissionHandler.requestPermissionWrapper())
                .thenAnswer({ thenPermissionDenied() })

        failurePermissionHandler.requestPermissionWrapper()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)

        isFailureFunctionCalled = false // Reset the value

        // Check that they're indeed reset back to false
        assertFalse(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
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