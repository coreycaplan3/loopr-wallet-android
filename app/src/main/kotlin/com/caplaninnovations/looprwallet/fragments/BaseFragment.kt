package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.utilities.logv


/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 *
 *
 * Purpose of Class:
 */
abstract class BaseFragment : Fragment() {

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        logv("Saving instance state...")
    }

}
