package com.caplaninnovations.looprwallet.utilities

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log

/**
 * Created by Corey on 1/19/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
fun FragmentManager.findFragmentOrCreate(tag: String, fragmentTagPairs: List<Pair<String, () -> Fragment>>): Fragment {
    return this.findFragmentByTag(tag) ?: createFragmentByTag(tag, fragmentTagPairs)
}

fun createFragmentByTag(tag: String, fragmentTagPairs: List<Pair<String, () -> Fragment>>): Fragment {
    // The fragment must be in here, or else it's a developer error
    Log.d("createFragmentTag", "Creating \"$tag\" fragment")
    val fragment = fragmentTagPairs.find { it.first == tag }
    return fragment!!.second.invoke()
}