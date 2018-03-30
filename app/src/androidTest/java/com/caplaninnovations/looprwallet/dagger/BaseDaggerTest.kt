package com.caplaninnovations.looprwallet.dagger

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.view.View
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.application.TestLooprWalletApp
import com.caplaninnovations.looprwallet.extensions.logd
import com.caplaninnovations.looprwallet.extensions.removeAllListenersAndClose
import com.caplaninnovations.looprwallet.models.android.settings.LooprSecureSettings
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import io.realm.Realm
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import java.util.*
import java.util.concurrent.FutureTask
import javax.inject.Inject


/**
 * Created by Corey on 2/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class BaseDaggerTest {

    @Inject
    lateinit var walletClient: WalletClient

    lateinit var component: LooprTestComponent

    private val walletName = "loopr-test-${BaseDaggerTest::class.java.simpleName}"

    /**
     * This realm is only opened and used because in memory realms are WIPED after the last one is
     * closed. So, we keep this one open throughout the full length of the test.
     */
    private var inMemoryRealm: Realm? = null

    var wallet: LooprWallet? = null
        private set(value) {
            field = value
            value?.let {
                inMemoryRealm = createRealm()
            }
        }

    val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()

    @Before
    fun baseDaggerSetup() {
        val context = instrumentation.targetContext.applicationContext
        val app = (context as TestLooprWalletApp)
        component = app.looprDaggerComponent as LooprTestComponent
        component.inject(this)

        Intents.init()

        putDefaultWallet()
        waitForActivityToBeSetup()
    }

    open fun putDefaultWallet() {
        val privateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e493"

        assertTrue(walletClient.createWallet(walletName, privateKey))

        wallet = walletClient.getCurrentWallet()
        assertNotNull(wallet)

        logd("Created currentWallet...")
    }

    @After
    fun baseDaggerTearDown() {
        Intents.release()

        inMemoryRealm.removeAllListenersAndClose()

        wallet?.let { walletClient.removeWallet(it.walletName) }

        val field = LooprSecureSettings.Companion::class.java.getDeclaredField("looprSecureSettings")
        field.isAccessible = true
        field.set(null, null)

        logd("Reset LooprSecureSettings static instance")
    }

    protected fun createRealm(): Realm {
        return LooprWalletApp.dagger.realmClient.getInstance(wallet!!.walletName, wallet!!.realmKey)
    }

    /**
     * @param block The block to be run on the UI thread.
     */
    protected fun runBlockingUiCode(block: suspend () -> Unit) {
        val deferred = CompletableDeferred<Unit>()
        launch(UI) {
            try {
                block()
                deferred.complete(Unit)
            } catch (e: Throwable) {
                deferred.completeExceptionally(e)
            }
        }
        try {
            runBlocking { deferred.await() }
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    /**
     * @param activity The activity whose UI thread on which the given task will run
     * @param task The task whose runnable will run on the supplied UI thread
     * @param waitForAnimations True if there should be an extra 300 ms wait, in anticipation for
     * animations to finish or false not to wait.
     */
    fun waitForTask(activity: BaseActivity, task: FutureTask<*>, waitForAnimations: Boolean) {
        activity.runOnUiThread(task)
        task.get()
        if (waitForAnimations) {
            Thread.sleep(300)
        }
    }

    /**
     * Waits for the application to be idling and running. Useful when committing fragment
     * transactions, so we can guarantee tests have a properly setup fragment/dialog.
     */
    fun waitForActivityToBeSetup() {
        instrumentation.waitForIdleSync()
    }

    /**
     * Asserts that the active activity is the one provided
     */
    fun <T : Activity> assertActivityActive(activityClass: Class<T>, waitTime: Long = 5000) {
        Thread.sleep(waitTime)
        intended(hasComponent(ComponentName(getTargetContext(), activityClass)))
    }

    fun clickView(view: View) {
        Espresso.onView(Matchers.`is`(view)).perform(ViewActions.click())
    }

    /**
     * Checks that the two dates are within a certain millisecond range of each other.
     *
     * @param date1 The date that came before [date2]
     * @param date2 The date that came after [date1]
     * @param rangeMillis The range (in milliseconds) that should be applied to the two dates. This
     * range must be >= 0.
     */
    fun assertDatesWithRange(date1: Date, date2: Date, rangeMillis: Long) {
        assertTrue(rangeMillis >= 0)

        assertTrue(date1.time - date2.time >= -rangeMillis)
        assertTrue(date1.time - date2.time <= rangeMillis)
    }

}