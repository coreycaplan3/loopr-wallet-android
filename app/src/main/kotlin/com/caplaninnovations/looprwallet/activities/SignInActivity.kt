package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dialogs.ConfirmPasswordDialog
import com.caplaninnovations.looprwallet.fragments.signin.SignInSelectionFragment
import com.caplaninnovations.looprwallet.extensions.loge

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class SignInActivity : BaseActivity() {

    override val contentView: Int
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

        if(fragment is ConfirmPasswordDialog.Communicator) {
            fragment.onPasswordConfirmed()
        } else {
            loge("Invalid fragment type; could not cast to Communicator!", IllegalStateException())
        }
    }

}
