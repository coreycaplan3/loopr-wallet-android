package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.utilities.loge
import kotlinx.android.synthetic.main.activity_sign_in.*

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

    override val isSecurityActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restoreWalletButton.setOnClickListener {
            // TODO delete me and replace with real implementation
            val walletName = "corey"
            if (!securityClient.createWallet(walletName)) {
                loge("Could not create wallet!", IllegalStateException())
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}
