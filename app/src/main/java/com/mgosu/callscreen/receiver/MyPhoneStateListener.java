package com.mgosu.callscreen.receiver;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mgosu.callscreen.ui.activity.CallingActivity;
import com.mgosu.callscreen.ui.activity.MissCallActivity;
import com.mgosu.callscreen.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mgosu.callscreen.util.Constants.DISSMISS_CALL;
import static com.mgosu.callscreen.util.Constants.KEY_DATE_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_NAME_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_URI_PATH;
import static com.mgosu.callscreen.util.Constants.permission;
import static com.mgosu.callscreen.util.Constants.permission1;

class MyPhoneStateListener extends PhoneStateListener {
    private Context context;
    private String contactId, contactName;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private Date callStartTime;
    private static boolean isIncoming;

    public MyPhoneStateListener(Context context) {
        this.context = context;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        if (lastState == state) {
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
//                    onMissedCall(context, savedNumber, contactID, name, callStartTime);
                    callStartTime = new Date();
                    @SuppressLint("SimpleDateFormat") DateFormat writeFormat = new SimpleDateFormat("hh:mm aa");
                    String formattedDate = writeFormat.format(callStartTime);
                    Intent intent = new Intent(context, MissCallActivity.class);
                    intent.putExtra(KEY_NAME_CONTACT, contactName);
                    intent.putExtra(KEY_PHONE_CONTACT, incomingNumber);
                    intent.putExtra(KEY_DATE_CONTACT, formattedDate);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
//                else if (isIncoming) {
//                    onIncomingCallEnded(context, savedNumber, contactID, name, callStartTime, new Date());
//                } else {
//                    onOutgoingCallEnded(context, savedNumber, contactID, name, callStartTime, new Date());
//                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                context.sendBroadcast(new Intent(DISSMISS_CALL));
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Utils.hasPermissions(context, permission)) {
                        CallActivity(incomingNumber);
                    }
                } else {
                    if (Utils.hasPermissions(context, permission1)) {
                        CallActivity(incomingNumber);
                    }
                }

                break;
        }
        lastState = state;
    }

    private void CallActivity(String incomingNumber) {
        getContactName(context, incomingNumber);
        Intent intent = new Intent(context, CallingActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_NAME_CONTACT, contactName);
        intent.putExtra(KEY_PHONE_CONTACT, incomingNumber);
        intent.putExtra(KEY_URI_PATH, contactId);
        context.startActivity(intent);
    }

//    public String getContactName(final String phoneNumber, Context context) {
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
//
//        String[] name = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
//        String[] id = new String[]{ContactsContract.PhoneLookup._ID};
//
//        String contactName = "";
//        Cursor cursor = context.getContentResolver().query(uri, name, null, null, null);
//        Cursor cursor1 = context.getContentResolver().query(uri, id, null, null, null);
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                contactName = cursor.getString(0);
//            }
//            cursor.close();
//        }
//
//        return contactName;
//    }

    public void getContactName(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();
        }
    }
}
