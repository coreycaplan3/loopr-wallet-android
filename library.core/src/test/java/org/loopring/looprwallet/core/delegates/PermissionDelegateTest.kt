package org.loopring.looprwallet.core.delegates

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import org.loopring.looprwallet.core.activities.BaseActivity
import junit.framework.Assert.*
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
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
class PermissionDelegateTest {

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

    private lateinit var successPermissionDelegate: PermissionDelegate
    private lateinit var failurePermissionDelegate: PermissionDelegate

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


        successPermissionDelegate = Mockito.spy(PermissionDelegate(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        ))
        assertFalse(successPermissionDelegate.shouldRequestPermissionNow)

        failurePermissionDelegate = Mockito.spy(PermissionDelegate(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        ))
        assertFalse(failurePermissionDelegate.shouldRequestPermissionNow)

        setupMockingRequestPermissions()
    }

    @Test
    fun requestPermission() {
        successPermissionDelegate.requestPermission()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isSuccessFunctionCalled)

        // Reset the value back to false
        isSuccessFunctionCalled = false

        failurePermissionDelegate.requestPermission()
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
        successPermissionDelegate = PermissionDelegate(
                activity = activity,
                permission = PERMISSION_GRANTED_IMMEDIATELY,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        )

        successPermissionDelegate.requestPermission()

        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        failurePermissionDelegate = PermissionDelegate(
                activity = activity,
                permission = PERMISSION_GRANTED_IMMEDIATELY,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = false
        )

        failurePermissionDelegate.requestPermission()

        // Requesting the permission fails, but checking the permission returns GRANTED --> so,
        // failure from [requestPermission] is over-ruled!
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
    }

    @Test
    fun turnImmediateRequestingPermissionOn() {
        successPermissionDelegate = Mockito.spy(PermissionDelegate(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_SUCCESS,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        ))

        failurePermissionDelegate = Mockito.spy(PermissionDelegate(
                activity = activity,
                permission = PERMISSION_DENIED_IN_INITIAL_CHECK,
                requestCode = REQUEST_CODE_FOR_FAILURE,
                onPermissionGranted = this::successFunction,
                onPermissionDenied = this::failureFunction,
                shouldRequestPermissionNow = true
        ))

        setupMockingRequestPermissions()

        assertTrue(successPermissionDelegate.shouldRequestPermissionNow)

        successPermissionDelegate.requestPermission()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
        isSuccessFunctionCalled = false // Reset its value

        assertTrue(failurePermissionDelegate.shouldRequestPermissionNow)
        failurePermissionDelegate.requestPermission()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)
    }

    // MARK - Private Methods

    private fun setupMockingRequestPermissions() {
        Mockito.`when`(successPermissionDelegate.requestPermissionWrapper())
                .thenAnswer({ thenPermissionGranted() })

        successPermissionDelegate.requestPermissionWrapper()
        assertTrue(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)

        isSuccessFunctionCalled = false // Reset the value

        Mockito.`when`(failurePermissionDelegate.requestPermissionWrapper())
                .thenAnswer({ thenPermissionDenied() })

        failurePermissionDelegate.requestPermissionWrapper()
        assertTrue(isFailureFunctionCalled)
        assertFalse(isSuccessFunctionCalled)

        isFailureFunctionCalled = false // Reset the value

        // Check that they're indeed reset back to false
        assertFalse(isSuccessFunctionCalled)
        assertFalse(isFailureFunctionCalled)
    }

    private fun thenPermissionGranted() {
        successPermissionDelegate.onRequestPermissionsResult(
                REQUEST_CODE_FOR_SUCCESS,
                arrayOf(PERMISSION_DENIED_IN_INITIAL_CHECK),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
        )
    }

    private fun thenPermissionDenied() {
        successPermissionDelegate.onRequestPermissionsResult(
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