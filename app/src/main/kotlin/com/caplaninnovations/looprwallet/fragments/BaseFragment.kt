package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.realm.LooprRealm
import com.caplaninnovations.looprwallet.utilities.logv
import io.realm.Realm


/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 *
 *
 * Purpose of Class:
 */
abstract class BaseFragment : Fragment() {

    lateinit var realm: Realm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO
        realm = LooprRealm.get("realm-name", ByteArray(1))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        logv("Saving instance state...")
    }

    override fun onDestroyView() {
        realm.removeAllChangeListeners()
        realm.close()

        super.onDestroyView()
    }

}
