package com.entertainment.fantom

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull


class SharedPrefUtil(val context: Context?) {

    val sharedPrefs = context?.applicationContext
           ?.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE)

     companion object{
         private const val PRIVATE_MODE = Context.MODE_PRIVATE
         private const val PREFERENCES_NAME = "sharedPref"

     }

    fun getContext() {
        context
    }
    @NonNull
    private fun getEditor(): SharedPreferences.Editor? {
        return sharedPrefs!!.edit()
    }

     fun get(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPrefs!!.getBoolean(key, defaultValue)
    }

    fun put(key: String, value: Boolean) {
        getEditor()!!.putBoolean(key, value).commit()
    }
}