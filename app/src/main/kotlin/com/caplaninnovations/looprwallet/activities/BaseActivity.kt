package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.caplaninnovations.looprwallet.R
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO set theme dynamically (before call to setContentView)
        applicationContext.setTheme(R.style.AppTheme_Light)
        this.setTheme(R.style.AppTheme_Light)

        setContentView(contentView)
        setSupportActionBar(toolbar)
    }

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean = true

}