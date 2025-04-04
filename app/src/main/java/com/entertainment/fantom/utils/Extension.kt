package com.entertainment.fantom.utils


import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.entertainment.fantom.R

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

object UiUtils {
    @JvmStatic
    fun hideToolbarAndDrawer(activity: AppCompatActivity) {
        activity.supportActionBar?.hide()
        activity.findViewById<DrawerLayout>(R.id.drawer_layout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    @JvmStatic
    fun showToolbarAndDrawer(activity: AppCompatActivity) {
        activity.supportActionBar?.show()
        activity.findViewById<DrawerLayout>(R.id.drawer_layout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}