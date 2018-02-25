package com.caplaninnovations.looprwallet.dagger

import android.support.test.InstrumentationRegistry
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.application.TestLooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import junit.framework.Assert.assertTrue
import org.junit.Before
import java.util.concurrent.FutureTask
import javax.inject.Inject
import junit.framework.Assert.assertNotNull


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
    lateinit var walletSettings: WalletSettings

    lateinit var component: LooprTestComponent

    @Before
    fun baseDaggerSetup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val app = (context as TestLooprWalletApp)
        component = app.provideDaggerComponent() as LooprTestComponent
        component.inject(this)

        putDefaultWallet()
    }

    open fun putDefaultWallet() {
        val walletName = "loopr-test"
        val privateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e493"
        val didCreateWallet = walletSettings.createWallet(walletName, privateKey)
        assertTrue(didCreateWallet)
    }

    /**
     * @param activity The activity whose UI thread on which the given task will run
     * @param task The task whose runnable will run on the supplied UI thread
     * @param waitForAnimations True if there should be an extra 300 ms wait, in anticipation for
     * animations to finish or false not to wait.
     */
    fun waitForAnimationsAndTask(activity: BaseActivity, task: FutureTask<*>, waitForAnimations: Boolean) {
        activity.runOnUiThread(task)
        task.get()
        if (waitForAnimations) {
            Thread.sleep(300)
        }
    }

    fun <T : BaseActivity> waitForActivityToStartAndFinish(activityClass: Class<T>) {
        val activityMonitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(activityClass.name, null, false)

        val activity = InstrumentationRegistry.getInstrumentation()
                .waitForMonitorWithTimeout(activityMonitor, 5000)

        assertNotNull(activity)
        activity.finish()
    }

}