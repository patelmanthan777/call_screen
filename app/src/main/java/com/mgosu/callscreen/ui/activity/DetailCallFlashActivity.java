package com.mgosu.callscreen.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.model.DownloadStatus;
import com.mgosu.callscreen.data.model.MyContact;
import com.mgosu.callscreen.data.roomdatabase.RoomDb;
import com.mgosu.callscreen.databinding.ActivityCallflashDetailBinding;
import com.mgosu.callscreen.service.download.DownloadService;
import com.mgosu.callscreen.ui.wiget.muticontact.ContactResult;
import com.mgosu.callscreen.ui.wiget.muticontact.LimitColumn;
import com.mgosu.callscreen.ui.wiget.muticontact.MultiContactPicker;
import com.mgosu.callscreen.util.PreferencesUtils;
import com.mgosu.callscreen.util.RootPath;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.io.File;
import java.util.ArrayList;

import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;
import static com.mgosu.callscreen.util.Constants.KEY_DEFAUL;
import static com.mgosu.callscreen.util.Constants.KEY_REQUEST_CONTACT;
import static com.mgosu.callscreen.util.Constants.KEY_REQUEST_PERMISSION;
import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.permission;
import static com.mgosu.callscreen.util.Constants.permission1;
import static com.mgosu.callscreen.util.Constants.permission_contact;

public class DetailCallFlashActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CONTACT_PICKER_REQUEST = 991;
    private ActivityCallflashDetailBinding binding;
    private ArrayList<ContactResult> results = new ArrayList<>();
    private RoomDb roomDb;
    private RootPath rootPath;
    private File localFile;
    private CallFlash callFlash;
    private PreferencesUtils preferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_callflash_detail);
        binding.myProgressBarLoading.setVisibility(View.VISIBLE);
        preferencesUtils = PreferencesUtils.getInstance(this);
        roomDb = RoomDb.getInstance(this);
        rootPath = RootPath.getInstance(this);
        getDataIntent();
        readDataRoom();
        initView();
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadService.KEY_ACTION));

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                DownloadStatus downloadStatus =
                        (DownloadStatus) bundle.getSerializable(DownloadService.KEY_DATA);
                if (downloadStatus != null && downloadStatus.getItemId().equals(callFlash.getItemId())) {
                    if (downloadStatus.isComplete()) {
                        updateDownloadButton();
                    } else {
                        binding.myProgressBar.setProgress(downloadStatus.getProgress());
                        binding.myTextViewDownload.setText(downloadStatus.getProgress() + "%");
                    }

                }
            }
        }
    };


    private void updateDownloadButton() {
        binding.myProgressBar.setProgress(100);
        boolean fileIsExisting = localFile.exists();
        if (fileIsExisting) {
            binding.myLayoutDownloadStatus.setVisibility(View.GONE);
            binding.myLayoutSetCallFlash.setVisibility(View.VISIBLE);
            binding.myVideoView.setVideoPath(localFile.getAbsolutePath());
            binding.myVideoView.seekTo(1);
            binding.myVideoView.start();
            binding.myVideoView.setVisibility(View.VISIBLE);
            binding.myImageViewThumb.setVisibility(View.GONE);
            binding.myProgressBarLoading.setVisibility(View.GONE);
        } else {
            binding.myLayoutDownloadStatus.setVisibility(View.VISIBLE);
            binding.myLayoutSetCallFlash.setVisibility(View.GONE);
            binding.myTextViewDownload.setText(getString(R.string.download_for_free));
            binding.myVideoView.setVisibility(View.GONE);
            binding.myImageViewThumb.setVisibility(View.VISIBLE);
        }
    }

    private void readDataRoom() {
        CallFlash temp =
                roomDb.callFlashDao().getCallSplashByItemId(callFlash.getItemId());
        binding.myLikeButton.setLiked(temp != null);
        binding.myLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Utils.submit2Server(true, callFlash.getItemId());
                roomDb.callFlashDao().insertCallSplash(callFlash);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                roomDb.callFlashDao().deleteByItemId(callFlash.getItemId());
            }
        });
    }

    private void initView() {
        binding.myImageButtonBack.setOnClickListener(this);
        binding.myButtonSetCallFlash.setOnClickListener(this);
        binding.myImageButtonSelectContact.setOnClickListener(this);
        binding.myTextViewDownload.setOnClickListener(this);
        loadImage();
        binding.myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                binding.myVideoView.start();
            }
        });
    }

    private void getDataIntent() {
        callFlash = (CallFlash) getIntent().getSerializableExtra(KEY_SEND_OBJ);
        localFile =
                new File(rootPath.getCallFlashFolder() + "/" + Utils.getFileNameFromPath(callFlash.getFlashUrl()));

    }

    private void loadImage() {
        Glide.with(this).load(callFlash.getThumbLarge()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target, DataSource dataSource,
                                           boolean isFirstResource) {
                binding.myProgressBarLoading.setVisibility(View.GONE);
                return false;
            }
        }).into(binding.myImageViewThumb);

    }

    private void startDownLoad() {
        Intent intent = new Intent(this, DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DownloadService.KEY_DATA, callFlash);
        bundle.putInt(DownloadService.KEY_TYPE, DownloadService.TYPE_CALL_FLASH);
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myImageButtonBack:
                onBackPressed();
                break;
            case R.id.myTextViewDownload:
                startDownLoad();
                break;
            case R.id.myButtonSetCallFlash:
//                showComingSoon();
                setCallFlash();
                break;
            case R.id.myImageButtonSelectContact:
                if (!Utils.hasPermissions(this, permission_contact)) {
                    Utils.permission(this, permission_contact, KEY_REQUEST_CONTACT);
                } else {
                    new MultiContactPicker.Builder(DetailCallFlashActivity.this)
                            .theme(R.style.MyCustomPickerTheme)
                            .hideScrollbar(false)
                            .showTrack(true)
                            .searchIconColor(Color.WHITE)
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                            .handleColor(ContextCompat.getColor(DetailCallFlashActivity.this,
                                    R.color.colorPrimary))
                            .bubbleColor(ContextCompat.getColor(DetailCallFlashActivity.this,
                                    R.color.colorPrimary))
                            .bubbleTextColor(Color.WHITE)
                            .setTitleText("Select MyContact")
                            .setLoadingType(MultiContactPicker.LOAD_ASYNC)
                            .limitToColumn(LimitColumn.NONE)
                            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out)
                            .showPickerForResult(CONTACT_PICKER_REQUEST);
                }
                break;

        }
    }

    private void setCallFlash() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (!Utils.hasPermissions(this, permission)) {
                Utils.permission(this, permission, KEY_REQUEST_PERMISSION);
            } else {
                setAppDefault();
            }
        }else {
            if (!Utils.hasPermissions(this, permission1)) {
                Log.e("111", "setCallFlash: " );
                Utils.permission(this, permission1, KEY_REQUEST_PERMISSION);
            } else {
                Log.e("112", "setCallFlash: 1" );
                setAppDefault();
            }
        }


    }

    private void setVideoSuccess() {
        preferencesUtils.setUrlVideoPath(localFile.getAbsolutePath());
        preferencesUtils.setStateCallFlashSetting(true);
        ToastUtils.getInstance(this).showMessage(getString(R.string.success_callflash));
    }

    private void showComingSoon() {
        ToastUtils.getInstance(this).showMessage("Coming Soon");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case KEY_REQUEST_CONTACT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            case KEY_REQUEST_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        if (Utils.hasPermissions(this,permission)){
                            setAppDefault();
                        }else {
                            Log.e("dsdfa", "onRequestPermissionsResult: 1" );
                            ToastUtils.getInstance(this).showMessage(getString(R.string.need_permission));
                        }
                    }else {
                        if (Utils.hasPermissions(this,permission1)){
                            setAppDefault();
                        }else {
                            Log.e("dsdfa", "onRequestPermissionsResult: 2" );
                            ToastUtils.getInstance(this).showMessage(getString(R.string.need_permission));
                        }
                    }

                } else {
                    Log.e("dsdfa", "onRequestPermissionsResult: 3" );
                    ToastUtils.getInstance(this).showMessage(getString(R.string.need_permission));
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results.addAll(MultiContactPicker.obtainResult(data));
                for (int i = 0; i < results.size(); i++) {
                    MyContact myContact = roomDb.contactDao().ContactById(Integer.parseInt(results.get(i).getContactID()));
                    if (myContact != null) {
                        roomDb.contactDao().updateContacts(new MyContact(myContact.getId(), localFile.getAbsolutePath()));
                    } else {
                        roomDb.contactDao().insertContacts(new MyContact(Integer.valueOf(results.get(i).getContactID()), localFile.getAbsolutePath()));
                    }
                }

//                if (results.size() > 0) {
//                    List<MyContact> allData = roomDb.contactDao().listContact();
//                    boolean isContactExits = false;
//                    for (ContactResult result : results) {
//                        if (allData.size() > 0) {
//                            for (MyContact base : allData) {
//                                if (Integer.valueOf(result.getContactID()) == base.getId()) {
//                                    isContactExits = true;
//                                } else {
//                                    isContactExits = false;
//                                }
//                                if (isContactExits) {
//                                    roomDb.contactDao().updateContacts(new MyContact(Integer
//                                    .valueOf(result.getContactID()), nameImage));
//                                } else {
//                                    try {
//                                        roomDb.contactDao().insertContacts(new MyContact
//                                        (Integer.valueOf(result.getContactID()), nameImage));
//                                    } catch (Exception x) {
//                                        roomDb.contactDao().updateContacts(new MyContact
//                                        (Integer.valueOf(result.getContactID()), nameImage));
//                                    }
//                                }
//                            }
//
//                        } else {
//                            roomDb.contactDao().insertContacts(new MyContact(Integer.valueOf
//                            (result.getContactID()), nameImage));
//                        }
//                    }
//
//                }
            } else if (resultCode == RESULT_CANCELED) {
            }

        }
        if (requestCode == KEY_DEFAUL) {
            if (resultCode == Activity.RESULT_OK) {
                setVideoSuccess();
            } else {
                ToastUtils.getInstance(this).showMessage(getString(R.string.accept_change));
            }
        }
    }

    private void setAppDefault() {
        TelecomManager telecomManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
                Intent intent12 = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                        .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent12, KEY_DEFAUL);
                Log.e("113", "setAppDefault: " );
            } else {
                Log.e("114", "setAppDefault: " );
                setVideoSuccess();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDownloadButton();
    }
}
