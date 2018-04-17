package org.loopring.looprwallet.core.models.android.architecture

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry


/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To integrate the lifecycle for fragments to hedge against performance issues.
 * This mainly has to do with onCreateView not being considered the life cycle event for creation
 * and onViewDestroyed for not being considered the life cycle event for destruction.
 *
 * See the fragment life cycle and [this Medium article](https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808)
 * for more information.
 *
 */
class FragmentViewLifecycleOwner : LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }
}