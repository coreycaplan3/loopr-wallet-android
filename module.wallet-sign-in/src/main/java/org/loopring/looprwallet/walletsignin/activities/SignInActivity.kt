package org.loopring.looprwallet.walletsignin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.walletsignin.fragments.signin.SignInSelectionFragment

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class SignInActivity : BaseActivity() {

    companion object {

        fun route(activity: Activity) {
            val intent = Intent(activity, SignInActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override val contentViewRes: Int
        get() = R.layout.activity_sign_in

    override val isSecureActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(SignInSelectionFragment(), SignInSelectionFragment.TAG)
        }
    }

    /**
     * Called when the user confirms their password for something.
     *
     * @param fragmentTag The fragment tag to which the user will be returned, confirming that they
     * indeed confirmed their password
     */
    fun onPasswordConfirmed(fragmentTag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (fragment is ConfirmPasswordDialog.OnPasswordConfirmedListener) {
            fragment.onPasswordConfirmed()
        } else {
            loge("Invalid fragment type; could not cast to OnPasswordConfirmedListener!", IllegalStateException())
        }
    }

}
