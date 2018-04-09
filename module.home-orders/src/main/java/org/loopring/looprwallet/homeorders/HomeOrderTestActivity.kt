package org.loopring.looprwallet.homeorders

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.activities.CoreTestActivity
import org.loopring.looprwallet.core.views.OrderProgressView
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey on 4/9/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class HomeOrderTestActivity: CoreTestActivity() {

    override fun executeHumanTestCode(savedInstanceState: Bundle?) {
        super.executeHumanTestCode(savedInstanceState)

//        val testRecyclerView = findViewById<RecyclerView>(R.id.testRecyclerView)
//        testRecyclerView.adapter = GeneralOrderAdapter(true)
//        testRecyclerView.layoutManager = LinearLayoutManager(this)

        val orderProgressView = findViewById<OrderProgressView>(R.id.orderProgressView)
        orderProgressView.setOnClickListener {
            orderProgressView.progress += 10
        }
    }

}