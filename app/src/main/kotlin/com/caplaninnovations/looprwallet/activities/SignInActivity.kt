package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject

class SignInActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_sign_in

    override val isSecurityActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restoreWalletButton.setOnClickListener {
            val settings = LooprWalletSettings(looprSettings)

            // TODO delete me
            val walletName = "corey"
            settings.createWallet(walletName)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}
