package com.mgosu.callscreen.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ITelephonyService extends Service {
    public void silenceRinger() {
    }

    public void answerRingingCall() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
