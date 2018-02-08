package com.caplaninnovations.looprwallet.utilities

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair

/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */
fun FragmentManager.findFragmentByTagOrCreate(tag: String, fragmentTagPairs: List<BottomNavigationFragmentPair>): Fragment {
    return this.findFragmentByTag(tag) ?: createFragmentByTag(tag, fragmentTagPairs)
}

private fun createFragmentByTag(tag: String, fragmentTagPairs: List<BottomNavigationFragmentPair>): Fragment {
    // The fragment must be in here, or else it's a developer error
    Log.i("createFragmentTag", "Creating \"$tag\" fragment")
    val fragment = fragmentTagPairs.find { it.tag == tag }
    return fragment!!.fragment
}