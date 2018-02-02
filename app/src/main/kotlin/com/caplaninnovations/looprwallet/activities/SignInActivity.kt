package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_sign_in

    override val isSecurityActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restoreWalletButton.setOnClickListener {
            val settings = LooprWalletSettings(this)

            val walletName = "corey"
            settings.createWallet(walletName)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}
