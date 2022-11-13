package com.entertainment.fantom.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    public static ProgressDialog progressDialog(Context context) {
        return ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
    }
}
