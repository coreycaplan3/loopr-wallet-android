package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
<<<<<<< HEAD:app/src/main/kotlin/com/caplaninnovations/looprwallet/activities/MarketsActivity.kt
=======
import android.os.Parcelable
>>>>>>> 12e547237acd7d4b7659e7967e691651d4490970:app/src/main/kotlin/com/caplaninnovations/looprwallet/fragments/MarketsParentFragment.kt
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.caplaninnovations.looprwallet.R
<<<<<<< HEAD:app/src/main/kotlin/com/caplaninnovations/looprwallet/activities/MarketsActivity.kt
import com.caplaninnovations.looprwallet.fragments.AllMarketsFragment
import com.caplaninnovations.looprwallet.fragments.FavoriteMarketsFragment
import com.caplaninnovations.looprwallet.utilities.str
import kotlinx.android.synthetic.main.activity_markets.*

class MarketsActivity : BottomNavigationActivity() {

    override val navigationContainer: Int
        get() = R.layout.activity_markets
=======
import com.caplaninnovations.looprwallet.activities.BottomNavigationActivity
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.fragment_markets_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class MarketsParentFragment : BaseFragment(), BottomNavigationActivity.OnBottomNavigationReselectedLister {

    private var marketsTabs: TabLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_markets_parent, container, false)
    }
>>>>>>> 12e547237acd7d4b7659e7967e691651d4490970:app/src/main/kotlin/com/caplaninnovations/looprwallet/fragments/MarketsParentFragment.kt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

<<<<<<< HEAD:app/src/main/kotlin/com/caplaninnovations/looprwallet/activities/MarketsActivity.kt
        marketsViewPager.adapter = MarketsFragmentStatePagerAdapter(supportFragmentManager)
        marketsTabs.setupWithViewPager(marketsViewPager)
=======
        marketsViewPager.adapter = MarketsFragmentStatePagerAdapter(childFragmentManager)

        marketsTabs = activity?.findViewById(R.id.marketsTabs)
        marketsTabs?.setupWithViewPager(marketsViewPager)

        marketsTabs?.tabTextColors = context?.getAttrColorStateList(R.attr.tabWidgetTextColor)
    }

    override fun onResume() {
        super.onResume()

        context?.let {
            marketsTabs?.animateFromTranslationY(it.getResourceIdFromAttrId(android.R.attr.actionBarSize))
        }
    }

    override fun onPause() {
        super.onPause()

        context?.let {
            marketsTabs?.animateToTranslationY(it.getResourceIdFromAttrId(android.R.attr.actionBarSize))
        }
>>>>>>> 12e547237acd7d4b7659e7967e691651d4490970:app/src/main/kotlin/com/caplaninnovations/looprwallet/fragments/MarketsParentFragment.kt
    }

    override val selectedTab: String
        get() {
            return tagMarkets
        }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // TODO
    }

    class MarketsFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AllMarketsFragment()
                1 -> FavoriteMarketsFragment()
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> str(R.string.all)
                1 -> str(R.string.favorites)
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            // TODO
        }

        override fun saveState(): Parcelable? {
            return super.saveState()
        }

    }

}
