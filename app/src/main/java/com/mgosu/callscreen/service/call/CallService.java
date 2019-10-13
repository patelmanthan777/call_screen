package com.mgosu.callscreen.service.call;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import com.mgosu.callscreen.data.model.OngoingCall;
import com.mgosu.callscreen.ui.activity.CallToActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mgosu.callscreen.util.Constants.ACTION_DISCONNECT;
import static com.mgosu.callscreen.util.Constants.KEY_NAME_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_URI_PATH;

@TargetApi(Build.VERSION_CODES.M)
public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        if (call.getState() != Call.STATE_RINGING) {
            CallToActivity.start(this, call);
        }
//        else if (call.getState() == Call.STATE_RINGING){
//           call.getParent().
//            call.getDetails().get
//            Intent intent = new Intent("android.intent.action.ANSWER");
//            intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(KEY_NAME_CONTACT, name);
//            intent.putExtra(KEY_PHONE_CONTACT, number);
//            intent.putExtra(KEY_URI_PATH, contactID);
//            startActivity(intent);
//        }


    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
        if (call.getState() == Call.STATE_DISCONNECTED) {
            sendBroadcast(new Intent(ACTION_DISCONNECT));
        }
    }
}
