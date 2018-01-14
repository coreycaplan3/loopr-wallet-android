package caplaninnovations.com.looprwallet.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import caplaninnovations.com.looprwallet.R

/**
 * As of right now, this will translate into the *Market* screen for traders, when they first
 * open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}
