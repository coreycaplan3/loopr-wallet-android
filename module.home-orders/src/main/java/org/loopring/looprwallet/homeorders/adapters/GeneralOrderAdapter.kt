package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.isSameDay
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.realm.RealmClient
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.dagger.homeOrdersLooprComponent
import javax.inject.Inject

/**
 * Created by Corey Caplan on 4/6/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display orders that appear on the home screen to the user
 *
 * @param isOpen True if this adapter will be showing open orders or false if it'll be past ones.
 */
class GeneralOrderAdapter(private val isOpen: Boolean, activity: BaseActivity) : BaseRealmAdapter<EthToken>() {

    companion object {
        const val TYPE_FILTER = 3
    }

    private val activity by weakReference(activity)

    @Inject
    lateinit var realmClient: RealmClient

    init {
        homeOrdersLooprComponent.inject(this)
        runBlocking {
            delay(1000)

            val data = realmClient.getSharedInstance().where<EthToken>().findAll()
            updateData(data)
        }
    }

    override val totalItems: Int?
        get() = null

    override fun getItemViewType(position: Int): Int {
        val type = super.getItemViewType(position)

        return when (type) {
            TYPE_DATA -> when (position) {
                0 -> TYPE_FILTER
                else -> TYPE_DATA
            }
            else -> type
        }
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyGeneralOrderViewHolder(isOpen, parent)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return GeneralOrderViewHolder(parent.inflate(R.layout.view_holder_general_order))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: EthToken) {
        (holder as? EmptyGeneralOrderViewHolder)?.bind()

        val previousIndex = index - 1
        val previousItemIndex = index - 2 // There's an offset of 1 for the filter

        val showDateHeader = when {
            previousIndex == 0 ->
                // We're at the first item in the data-list
                true
            previousItemIndex >= 0 -> {
                val data = data
                // The item is NOT the SAME day as the previous one
                data != null && !data[previousItemIndex].lastUpdated.isSameDay(item.lastUpdated)
            }
            else -> false
        }

        (holder as? GeneralOrderViewHolder)?.bind("", showDateHeader) {
            activity?.supportFragmentManager
        }
    }

}