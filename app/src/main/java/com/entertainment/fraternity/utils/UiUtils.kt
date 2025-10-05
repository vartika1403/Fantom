package com.entertainment.fraternity.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.entertainment.fraternity.R

object UiUtils {
    private var categoryList: List<String> = mutableListOf()

    @JvmStatic
    fun hideToolbarAndDrawer(activity: AppCompatActivity) {
        activity.supportActionBar?.hide()
        activity.findViewById<DrawerLayout>(R.id.drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    @JvmStatic
    fun showToolbarAndDrawer(activity: AppCompatActivity) {
        activity.supportActionBar?.show()
        activity.findViewById<DrawerLayout>(R.id.drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    @JvmStatic
    fun saveCategories(list: List<String>) {
        categoryList = list;
    }

    @JvmStatic
    fun getCategories(): List<String> {
        return categoryList
    }

}
