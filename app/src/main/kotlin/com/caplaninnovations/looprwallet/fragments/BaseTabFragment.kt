package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BottomNavigationActivity
import com.caplaninnovations.looprwallet.utilities.getAttrColorStateList
import com.caplaninnovations.looprwallet.utilities.logv

/**
 * Created by Corey Caplan on 1/24/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseTabFragment : BaseFragment() {

    abstract var tabLayout: TabLayout?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(tabLayout == null) {
            throw IllegalStateException("Tabs must be initialized in onCreateView()!")
        }

        tabLayout?.tabTextColors = context?.getAttrColorStateList(R.attr.tabWidgetTextColor)
    }

    override fun onResume() {
        super.onResume()
        logv("${this::class.java.simpleName}: Showing tab layout...")

        (activity as? BottomNavigationActivity)?.showTabLayout(tabLayout)
    }

    override fun onPause() {
        super.onPause()

        logv("${this::class.java.simpleName}: Hiding tab layout...")
        (activity as? BottomNavigationActivity)?.hideTabLayout(tabLayout)
    }

}