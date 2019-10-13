package com.mgosu.callscreen.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.model.OngoingCall;
import com.mgosu.callscreen.databinding.ActivityCallToBinding;
import com.mgosu.callscreen.util.Utils;

import java.io.InputStream;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

import static com.mgosu.callscreen.util.Constants.ACTION_DISCONNECT;
import static com.mgosu.callscreen.util.Constants.KEY_CALL;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE;

public class CallToActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int TIME = 1000;
    private ActivityCallToBinding binding;
    private CompositeDisposable disposables;
    private OngoingCall ongoingCall;
    private String number;
    private int time = 0;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            time++;
            binding.tvPhoneCall.setText(Utils.getCallingTime(time));
            timeHandler.postDelayed(this, TIME);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_to);
        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();
        binding.myButtonEndCall.setOnClickListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String stringExtra = getIntent().getStringExtra(KEY_CALL) + "";
        if (stringExtra.equalsIgnoreCase("call")) {
            number = getIntent().getStringExtra(KEY_PHONE);
            binding.tvPhoneCall.setText(Utils.getCallingTime(time));
            timeHandler.postDelayed(timeRunnable, TIME);
        } else {
            number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
            binding.tvPhoneCall.setText(number);
        }
        retrieveContactPhoto(this, number);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_DISCONNECT:
//                        finish();
                        finishAndRemoveTask();
                        break;
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(ACTION_DISCONNECT);
        registerReceiver(receiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallToActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }

    public void retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        String contactName = null;
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

//        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.ic_launcher_background);
        Bitmap photo = null;

        if (contactId != null) {
            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                assert inputStream != null;
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("////", "id: " + contactId);
        Log.e("////", "name: " + contactName);
//        if (photo != null)
//            binding.imageView.setImageBitmap(photo);

        if (contactName != null && !contactName.isEmpty())
            binding.tvNameCall.setText(contactName);
        else binding.tvNameCall.setText("Unknow");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myButtonEndCall:
                ongoingCall.hangup();
                finishAndRemoveTask();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
