package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Created by Corey on 1/14/2018.
 * Project: MeetUp
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO set theme (before call to setContentView)

        setContentView(contentView)
        setSupportActionBar(toolbar)
    }

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

}