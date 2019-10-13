package com.mgosu.callscreen.ui.util;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;

public class TimeDown extends CountDownTimer {
    private boolean isTurn = false;
    private CameraManager cameraManager;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public TimeDown(long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (cameraManager == null)
                cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTick(long millisUntilFinished) {
        if (!isTurn) {
            isTurn = true;
            turnOn();
        } else {
            isTurn = false;
            turnOff();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOn() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOff() {
        try {
            if (cameraManager != null) {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
            }

        } catch (CameraAccessException e) {
        }
    }

    @Override
    public void onFinish() {
        if (cameraManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                turnOff();
            }
            cameraManager = null;
        }

    }
}
