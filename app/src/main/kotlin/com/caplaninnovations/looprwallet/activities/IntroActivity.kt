package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.caplaninnovations.looprwallet.R

import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override val contentView: Int
        get() = R.layout.activity_intro

}
