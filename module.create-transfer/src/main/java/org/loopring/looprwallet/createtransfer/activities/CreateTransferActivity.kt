package org.loopring.looprwallet.createtransfer.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.createtransfer.R
import org.loopring.looprwallet.createtransfer.fragments.SelectTransferContactFragment
import org.loopring.looprwallet.createtransfer.viewmodels.CreateTransferViewModel

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateTransferActivity : BaseActivity() {

    companion object {

        private const val KEY_DEFAULT_ADDRESS = "_DEFAULT_ADDRESS"

        /**
         * Routes the current activity to the [CreateTransferActivity].
         *
         * @param fragment The current fragment
         * @param defaultAddress The default address to set the send edit text field to. Optional.
         */
        fun route(fragment: Fragment, defaultAddress: String? = null) {
            val intent = Intent(CoreLooprWalletApp.application, CreateTransferActivity::class.java)
                    .putExtra(KEY_DEFAULT_ADDRESS, defaultAddress)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            fragment.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_transfer

    override val isSignInRequired: Boolean
        get() = true

    private val createTransferViewModel by lazy {
        LooprViewModelFactory.get<CreateTransferViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Used to initialize the view model and warm it up before we get to the Amount Fragment
        createTransferViewModel.allTokens

        if (savedInstanceState == null) {
            val address = intent.getStringExtra(KEY_DEFAULT_ADDRESS)
            val fragment = SelectTransferContactFragment.getInstance(address)
            val tag = SelectTransferContactFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }
    }

}