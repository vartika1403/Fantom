package com.entertainment.fantom.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    public static ProgressDialog showProgressDialog(Context context, String title) {
        return ProgressDialog.show(context, title,
                "Loading. Please wait...", true);
    }

    public static void hideProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
