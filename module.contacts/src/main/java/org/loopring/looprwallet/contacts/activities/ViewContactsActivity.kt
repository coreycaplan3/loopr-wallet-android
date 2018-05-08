package org.loopring.looprwallet.contacts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.contacts.R
import org.loopring.looprwallet.contacts.fragments.ViewContactsFragment
import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey on 4/30/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewContactsActivity : BaseActivity() {

    companion object {

        fun route(activity: Activity) {
            activity.startActivity(Intent(activity, ViewContactsActivity::class.java))
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_view_contacts

    override val isSignInRequired: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(ViewContactsFragment.getViewAllInstance(), ViewContactsFragment.TAG)
        }
    }

}