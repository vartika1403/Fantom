package com.entertainment.fantom

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import butterknife.ButterKnife
import com.entertainment.fantom.databinding.ActivityHomeBinding
import com.entertainment.fantom.fragment.HomeFragment
import com.entertainment.fantom.fragment.LoginFragment
import com.entertainment.fantom.fragment.ProfileFragment


class HomeActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences? by lazy {
        this.getSharedPreferences("app", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)

        // Enabling Up / Back navigation
        loadHomeFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflating the menu XML to the menu object
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_profile -> {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment: Fragment = ProfileFragment.newInstance(DetailObject(), true)
                fragmentTransaction.replace(R.id.fragment_container, fragment)
                fragmentTransaction.addToBackStack(HomeFragment.TAG)
                fragmentTransaction.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadHomeFragment() {
        val fragmentManager = supportFragmentManager
        val isLogin = sharedPreferences?.getBoolean("isLogin", false) ?: false
        Log.d(TAG, "Login home: " + isLogin)
        val fragment: Fragment = if (isLogin) HomeFragment() else LoginFragment()
        val tag = fragment.javaClass.name
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
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