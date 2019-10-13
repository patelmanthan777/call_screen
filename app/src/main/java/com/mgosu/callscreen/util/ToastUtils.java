package com.mgosu.callscreen.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast toast;
    private static ToastUtils instance;

    private ToastUtils(Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static ToastUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ToastUtils(context);
        }
        return instance;
    }

    public void showMessage(String message) {
        toast.setText(message);
        toast.show();
    }

    public void showMessage(int messageId) {
        toast.setText(messageId + "");
        toast.show();
    }

}
