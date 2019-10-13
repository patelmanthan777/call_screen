package com.mgosu.callscreen.util;

import android.util.Log;

public class LogUtils {
    private static final boolean enableLog = true;

    public static void LOG_E(String TAG, String message) {
        if (enableLog) {
            Log.e(TAG, message);
        }
    }
}
