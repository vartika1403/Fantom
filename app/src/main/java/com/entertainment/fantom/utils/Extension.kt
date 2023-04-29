package com.entertainment.fantom.utils

import android.app.ProgressDialog
import android.content.Context
import android.util.Patterns

fun ProgressDialog.display(context: Context, title : String = "") : ProgressDialog {
    return  ProgressDialog.show(context, title, "Loading. Please wait...", true)
}

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
