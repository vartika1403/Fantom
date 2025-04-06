package com.entertainment.fantom

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import butterknife.ButterKnife
import com.entertainment.fantom.databinding.ActivityHomeBinding
import com.entertainment.fantom.utils.UiUtils.hideToolbarAndDrawer
import com.entertainment.fantom.utils.UiUtils.showToolbarAndDrawer
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private var isLogin : Boolean = false

    private val sharedPreferences: SharedPreferences? by lazy {
        this.getSharedPreferences("app", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        loadHomeFragment()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)

        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        if (navController.currentDestination?.id == R.id.loginFragment) {
            hideToolbarAndDrawer(this)
        } else {
            showToolbarAndDrawer(this)
        }

        setupNavigationItemSelectedListener(navigationView, navController)

    }

    private fun setupNavigationItemSelectedListener(navigationView: NavigationView,
                                                    navController: NavController) {

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logoutMenu -> {
                    handleLogout(navController)
                    true
                }
                R.id.userProfileFragmentMenu -> {
                    navController.navigate(R.id.userProfileFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.searchFragmentMenu -> {
                    navController.navigate(R.id.searchFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleLogout(navController: NavController) {
        isLogin = false

        val editor = sharedPreferences?.edit()
        editor?.putBoolean("isLogin", false)
        editor?.apply()

        navController.navigate(R.id.loginFragment)
        hideToolbarAndDrawer(this)
        Toast.makeText(this, getString(R.string.logout), Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_profile -> {
                navController.navigate(R.id.profileFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadHomeFragment() {
         isLogin = sharedPreferences?.getBoolean("isLogin", false) ?: false
        Log.d(TAG, "Login home: $isLogin")

        val navController = findNavController(R.id.nav_host_fragment)

        if (isLogin) {
            navController.navigate(R.id.homeFragment)
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        val currentDestination = navController.currentDestination?.id
        findViewById<Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE

        Log.d(TAG, "back press: current fragment id = $currentDestination")

        if (currentDestination == R.id.homeFragment) {
            finish()
        } else {
            navController.navigateUp()
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}