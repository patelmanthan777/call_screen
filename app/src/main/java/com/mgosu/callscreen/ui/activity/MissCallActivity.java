package com.mgosu.callscreen.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.ActivityMissCallBinding;

import static com.mgosu.callscreen.util.Constants.KEY_DATE_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_NAME_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE_CONTACT;

public class MissCallActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMissCallBinding binding;
    private String nameContact, phoneContact, timeMissCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_miss_call);
        binding.RlLayout.setOnClickListener(this);
        binding.linearCall.setOnClickListener(this);
        binding.linearMess.setOnClickListener(this);
        binding.linearMarked.setOnClickListener(this);
        binding.ivClose.setOnClickListener(this);
        getDataIntent();
//        getCountMissCall();
    }

    private void getDataIntent() {
        nameContact = getIntent().getStringExtra(KEY_NAME_CONTACT);
        phoneContact = getIntent().getStringExtra(KEY_PHONE_CONTACT);
        timeMissCall = getIntent().getStringExtra(KEY_DATE_CONTACT);
        if (nameContact != null && !nameContact.isEmpty()) {
            binding.tvNumber.setText(nameContact);
        } else {
            String s = PhoneNumberUtils.formatNumber(phoneContact);
            binding.tvNumber.setText(s);
        }
        binding.tvTime.setText(timeMissCall);

    }

    private void getCountMissCall() {
        String[] projection = {CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE};
        String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE;
        Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
        c.moveToFirst();
        Log.e("CALL", "" + c.getCount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RlLayout:
                finish();
                break;
            case R.id.linear_call:
                finish();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneContact));
                startActivity(intent);
                break;
            case R.id.linear_mess:
                finish();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"+phoneContact));
                sendIntent.putExtra("sms_body", "");
                startActivity(sendIntent);
                break;
            case R.id.linear_marked:
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }
}
