package com.entertainment.fantom.utils

import android.app.ProgressDialog
import android.content.Context
import android.opengl.Visibility
import android.view.View

fun ProgressDialog.display(context: Context, title : String = "") : ProgressDialog {
    return  ProgressDialog.show(context, title, "Loading. Please wait...", true)
}