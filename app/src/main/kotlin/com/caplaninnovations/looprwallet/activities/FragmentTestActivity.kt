package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class FragmentTestActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_test_container

    override val isSecurityActivity: Boolean
        get() = false

    fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.activityContainer, fragment, tag)
                .commitNow()

        if (fragment.retainInstance) {
            supportFragmentManager.beginTransaction()
                    .add(fragment, tag)
                    .commitNow()
        }
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commitNow()
    }
}