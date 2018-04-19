package org.loopring.looprwallet.viewbalances.activities

import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.fragments.ViewBalancesFragment

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
class ViewBalancesActivity: BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_view_balances

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(ViewBalancesFragment(), ViewBalancesFragment.TAG)
        }
    }

}