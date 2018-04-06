package org.loopring.looprwallet.core.dagger

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
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.removeAllListenersAndClose
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import io.realm.Realm
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.realm.RealmClient
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

    companion object {

        val testCoreLooprComponent: TestCoreLooprComponent by lazy {
            val context = CoreLooprWalletApp.context
            val component = DaggerCoreLooprComponent.builder()
                    .looprSettingsModule(LooprSettingsModule(context))
                    .looprSecureSettingsModule(LooprSecureSettingsModule(context))
                    .looprRealmModule(LooprRealmModule())
                    .looprWalletModule(LooprWalletModule(context))
                    .build()

            return@lazy DaggerTestCoreLooprComponent.builder()
                    .coreLooprComponent(component)
                    .build()
        }

    }

    @Inject
    lateinit var walletClient: WalletClient

    val walletName = "loopr-test-${BaseDaggerTest::class.java.simpleName}"

    protected lateinit var address: String
        private set

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

    @Inject
    lateinit var realmClient: RealmClient

    @Before
    fun baseDaggerSetup() {
        testCoreLooprComponent.inject(this)

        Intents.init()

        runBlockingUiCode {
            putDefaultWallet()
        }
        waitForActivityToBeSetup()
    }

    open fun putDefaultWallet() {
        val privateKey = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"

        assertTrue(walletClient.createWallet(walletName, privateKey))

        wallet = walletClient.getCurrentWallet()
        assertNotNull(wallet)

        address = wallet!!.credentials.address

        logd("Created currentWallet...")
    }

    @After
    fun baseDaggerTearDown() {
        Intents.release()

        runBlockingUiCode {
            inMemoryRealm.removeAllListenersAndClose()
        }

        wallet?.let { walletClient.removeWallet(it.walletName) }

        val field = LooprSecureSettings.Companion::class.java.getDeclaredField("looprSecureSettings")
        field.isAccessible = true
        field.set(null, null)

        logd("Reset LooprSecureSettings static instance")
    }

    protected fun createRealm(): Realm {
        return realmClient.getPrivateInstance(wallet!!.walletName, wallet!!.realmKey)
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