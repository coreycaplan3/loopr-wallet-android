package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.activities.BaseActivity


/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 *
 *
 * Purpose of Class:
 */
abstract class BaseFragment : Fragment() {

    abstract var container: ViewGroup?

    override fun onResume() {
        super.onResume()
        (activity as? BaseActivity)?.updateContainerBasedOnToolbarMode(container)
    }

}
