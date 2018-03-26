package com.caplaninnovations.looprwallet.fragments.security

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
import kotlinx.android.synthetic.main.number_pad.*

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
abstract class BaseSecurityFragment : BaseFragment() {

    /**
     * The type of security page to show.
     *
     * @see TYPE_PIN_SECURITY
     */
    private val securityType: String
        get() {
            return LooprWalletApp.dagger.securitySettings.getCurrentSecurityType()
        }

    final override val layoutResource: Int
        get() = when (securityType) {
            TYPE_PIN_SECURITY -> R.layout.fragment_security_pin
            else -> throw IllegalArgumentException("Invalid security type, found: $securityType")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Perform view-specific setup
        when (securityType) {
            TYPE_PIN_SECURITY -> {
                numberPadDecimal.visibility = View.GONE
            }
        }

    }

}