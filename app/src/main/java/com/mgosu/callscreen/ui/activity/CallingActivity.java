package com.mgosu.callscreen.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.CallManager;
import com.mgosu.callscreen.data.model.MyContact;
import com.mgosu.callscreen.data.model.OngoingCall;
import com.mgosu.callscreen.data.roomdatabase.RoomDb;
import com.mgosu.callscreen.databinding.ActivityCallingBinding;
import com.mgosu.callscreen.ui.util.TimeDown;
import com.mgosu.callscreen.util.PreferencesUtils;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

import static com.mgosu.callscreen.util.Constants.DISSMISS_CALL;
import static com.mgosu.callscreen.util.Constants.KEY_CALL;
import static com.mgosu.callscreen.util.Constants.KEY_NAME_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE;
import static com.mgosu.callscreen.util.Constants.KEY_PHONE_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_REQUEST_CAMERA;
import static com.mgosu.callscreen.util.Constants.KEY_URI_PATH;

//import

public class CallingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCallingBinding binding;
    private String nameContact, phoneContact, contactID, pathFileVideo;
    private String TAG = "CallingActivity";
    private String[] permissionCamera = {Manifest.permission.CAMERA};
    private Camera camera;
    private boolean isFlast = false;
    private TelecomManager tm;
    private RoomDb roomDb;
    private CallManager callManager;
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private List<MyContact> listContactSave;
    private File[] listFileName;
    private List<MyContact> listContactById;
    private Uri uriPath;
    private CompositeDisposable disposables;
    private OngoingCall ongoingCall;
    private boolean b = true;
    private Thread thread;
    private TimeDown timeDown;
    private PreferencesUtils preferencesUtils;
    private static final int millisInFuture = 25000;
    private static final int countDownInterval = 500;
    private String videoPath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calling);
        roomDb = RoomDb.getInstance(this);
        preferencesUtils = PreferencesUtils.getInstance(this);
        createVariable();
        timeDown = new TimeDown(millisInFuture, countDownInterval, this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            getData();
//        else
        getDataIntent();
        if (preferencesUtils.getStateCallFlashSetting()){
            checkGetData();
            setVideo();
        }
        binding.ivFlash.setOnClickListener(this);
//        binding.ivVolume.setOnClickListener(this);
        binding.btCallStart.setOnClickListener(this);
        binding.btCallEnd.setOnClickListener(this);
        onDissMissPhone();
    }

    private void checkGetData() {
//        listContactSave = roomDb.contactDao().listContact();

//        File file = new File(PATH_VIDEO_FOLDER);
//        if (file.exists() && file.length() != 0) {
//            listFileName = file.listFiles();
//        }
//        if (contactID != null && contactID.length() != 0) {
//            listContactById = roomDb.contactDao().listContactById(Integer.valueOf(contactID));
//            Log.e(TAG, "checkGetData: " + listContactById.size());
//        }
//        if (listContactById != null && listContactById.size() != 0) {
//            for (File file1 : listFileName) {
//                Log.e(TAG, "vaopath 0");
//                if (listContactById.get(0).getNameFile().equalsIgnoreCase(file1.getName().replace(".mp4", ""))) {
//                    Log.e(TAG, "vaopath 1");
//                    uriPath = Uri.parse(file1.getAbsolutePath());
//                    break;
//                }
//            }
//        } else {
//            Log.e(TAG, "vao20: ");
//            listContactById = roomDb.contactDao().listContactById(ID_ALL_CONTACT);
//            if (listContactById != null && listContactById.size() != 0) {
//                Log.e(TAG, "vao 21: ");
//                for (File file1 : listFileName) {
//                    if (listContactById.get(0).getNameFile()
//                            .equalsIgnoreCase(file1.getName().replace(".mp4", "").trim())) {
//                        uriPath = Uri.parse(file1.getAbsolutePath());
//                        Log.e(TAG, "vao path 2 " + file.getAbsolutePath());
//                        break;
//                    }
//                }
//            }
//        }
        if (contactID != null && !contactID.isEmpty()) {
            MyContact myContact = roomDb.contactDao().ContactById(Integer.parseInt(contactID));
            if (myContact != null) {
                videoPath = myContact.getVideo_url();
            } else {
                String urlVideoPath = preferencesUtils.getUrlVideoPath();
                if (urlVideoPath != null && !urlVideoPath.isEmpty()) {
                    videoPath = urlVideoPath;
                }

            }
        } else {
            videoPath = preferencesUtils.getUrlVideoPath();
            if (videoPath.isEmpty()) {
                videoPath = pathFileVideo;
            }
        }
    }

    private void setVideo() {
        binding.videoView.setVideoPath(videoPath);
        binding.videoView.start();
        binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.videoView.seekTo(1);
                binding.videoView.start();
            }
        });
    }

    private void getDataIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ongoingCall = new OngoingCall();
        }
        disposables = new CompositeDisposable();

        nameContact = getIntent().getStringExtra(KEY_NAME_CONTACT);
        phoneContact = getIntent().getStringExtra(KEY_PHONE_CONTACT);
        contactID = getIntent().getStringExtra(KEY_URI_PATH);
        Log.e(TAG, "onCreate: " + contactID);
        if (contactID != null && !contactID.isEmpty()) {
            Uri photoUri = getPhotoUri(this, Long.valueOf(contactID));
//            Uri uri = Uri.parse(contactID);
            if (photoUri != null) {
                Glide.with(this).load(photoUri).into(binding.profileImage);
            }
        }
        if (phoneContact != null && !phoneContact.isEmpty()) {
            binding.phoneContact.setText(phoneContact);
        }
        if (nameContact != null && !nameContact.isEmpty())
            binding.nameContact.setText(nameContact);
        else binding.nameContact.setText("Unknow");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getData() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
        retrieveContactPhoto(this, number);

    }

    private void createVariable() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        pathFileVideo = "android.resource://" + getPackageName() + "/" + R.raw.abc;
//        uriPath = Uri.parse(pathFileVideo);
        roomDb = RoomDb.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        }
        callManager = new CallManager(this);
    }

    public Uri getPhotoUri(Context context, long contactId) {
        ContentResolver contentResolver = context.getContentResolver();

        try {
            Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " + ContactsContract.Data.MIMETYPE
                            + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                    null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffCamera();
        releaseReceiver();
        if (timeDown != null) {
            timeDown.onFinish();
            timeDown = null;
        }

    }

    private void releaseReceiver() {
        if (receiver != null && filter != null) {
            unregisterReceiver(receiver);
        }
    }

    private void onDissMissPhone() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case DISSMISS_CALL:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        } else
                            finish();
                        break;
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(DISSMISS_CALL);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivFlash:
                if (preferencesUtils.getChangeSwLed()) {
                    if (Utils.checkCamera(this)) {
                        if (!Utils.hasPermissions(this, permissionCamera)) {
                            Utils.permission(this, permissionCamera, KEY_REQUEST_CAMERA);
                        } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            if (!isFlast) {
//                                isFlast = true;
//                                blinkFlash();
//                            } else {
//                                isFlast = false;
//                                thread.currentThread().interrupt();
//                            }
//                        }
                            if (!isFlast) {
                                isFlast = true;
                                if (timeDown != null) {
                                    timeDown.start();
                                } else {
                                    timeDown = new TimeDown(millisInFuture, countDownInterval, this);
                                    timeDown.start();
                                }
                            } else {
                                timeDown.onFinish();
                                isFlast = false;
                                timeDown = null;
                            }

                        }
                    }
                } else {
                    ToastUtils.getInstance(this).showMessage(getString(R.string.turn_on_setting));
                }
                break;
            case R.id.btCallEnd:
                Log.e(TAG, "btCallEnd: ");
                Utils.rejectCall(this);
                if (timeDown != null) {
                    timeDown.onFinish();
                    timeDown = null;
                }
                break;
            case R.id.btCallStart:
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    ongoingCall.answer();
                } else {
                    callManager.acceptCall();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.e(TAG, "onClick: ");
                    finishAndRemoveTask();
                } else {
                    finish();
                }
                Intent intent = new Intent(this, CallToActivity.class);
                intent.putExtra(KEY_CALL, "call");
                intent.putExtra(KEY_PHONE, phoneContact);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
    }


    private void turnOnCamera() {
        isFlast = true;
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        camera.autoFocus(new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
            }
        });

    }
    private void turnOffCamera() {
        if (camera != null && isFlast) {
            camera.stopPreview();
            camera.release();
            isFlast = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case KEY_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnOnCamera();

                } else {
                    ToastUtils.getInstance(this).showMessage(getString(R.string.need_permission));
                }
                return;
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallingActivity.class)
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

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.image_people3);
//        Bitmap photo = null;

        if (contactId != null) {
            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                assert inputStream != null;
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (photo != null) binding.profileImage.setImageBitmap(photo);

        if (contactName != null)
            binding.nameContact.setText(contactName);
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        disposables.clear();
//    }
}
