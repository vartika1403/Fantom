package com.entertainment.fantom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.entertainment.fantom.R
import android.util.Log
import com.entertainment.fantom.SplashActivity
import java.lang.Runnable
import android.content.Intent
import android.os.Handler
import com.entertainment.fantom.HomeActivity

class SplashActivity : AppCompatActivity() {
    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 1000

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/Log.d(TAG, "count onCreateOf Splash, ")
        Handler().postDelayed({ /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this@SplashActivity, HomeActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(mainIntent)
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    companion object {
        private val TAG = SplashActivity::class.java.canonicalName
    }
}