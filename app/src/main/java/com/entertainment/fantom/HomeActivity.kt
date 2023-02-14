package com.entertainment.fantom

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import butterknife.ButterKnife
import android.view.Menu
import android.util.Log
import androidx.fragment.app.Fragment
import com.entertainment.fantom.databinding.ActivityHomeBinding
import com.entertainment.fantom.fragment.HomeFragment
import com.entertainment.fantom.fragment.LoginFragment

class HomeActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences? by lazy {
        this.getSharedPreferences("app", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        // Enabling Up / Back navigation
        //  getActionBar().setDisplayHomeAsUpEnabled(true);
        loadHomeFragment()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val actionBar = actionBar
        Log.d(TAG, "action bar, $actionBar")
        actionBar?.show()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun loadHomeFragment() {
        val fragmentManager = supportFragmentManager
        val isLogin = sharedPreferences?.getBoolean("isLogin", false) ?: false
        Log.d(TAG, "Login home: " + isLogin)
        val fragment: Fragment = if (isLogin) HomeFragment() else LoginFragment()
        val tag = fragment.javaClass.name
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.d(TAG, "count , $count fragment name: $this")
        if (count == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}