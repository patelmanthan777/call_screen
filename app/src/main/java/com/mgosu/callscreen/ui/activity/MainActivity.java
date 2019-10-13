package com.mgosu.callscreen.ui.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.view.View;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.ActivityMainBinding;
import com.mgosu.callscreen.service.CameraWallpaperService;
import com.mgosu.callscreen.ui.adapter.SliderAdapter;
import com.mgosu.callscreen.ui.wiget.slide.SliderAnimations;
import com.mgosu.callscreen.ui.wiget.slide.SliderView;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;
import static com.mgosu.callscreen.util.Constants.KEY_REQUEST_CAMERA;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    ActivityMainBinding mainBinding;
    private String[] permission_camera = {CAMERA, WRITE_CALL_LOG};
    private Integer[] list = {R.drawable.slide_image_1, R.drawable.slide_image_2,
            R.drawable.slide_image_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.RelativeLayoutCall.setOnClickListener(this);
        mainBinding.RelativeLayoutWallpapers.setOnClickListener(this);
        mainBinding.RelativeLayoutRingtone.setOnClickListener(this);
        mainBinding.RelativeLayoutSetting.setOnClickListener(this);
        mainBinding.ivHeart.setOnClickListener(this);
        mainBinding.ivShare.setOnClickListener(this);
        mainBinding.ivGift.setOnClickListener(this);
        mainBinding.ivGame.setOnClickListener(this);
        setupViews();

    }

    private void setupViews() {

        final SliderAdapter adapter = new SliderAdapter(this, list);
        adapter.setCount(3);

        mainBinding.imageSlider.setSliderAdapter(adapter);
        mainBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        mainBinding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        mainBinding.imageSlider.startAutoCycle();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RelativeLayoutCall:

                startActivity(new Intent(this, CallFlashActivity.class));

                break;
            case R.id.RelativeLayoutWallpapers:
                Intent intent = new Intent(this, WallPaperActivity.class);
                startActivity(intent);

                break;
            case R.id.RelativeLayoutRingtone:
                showComingSoon();
                break;
            case R.id.RelativeLayoutSetting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.ivHeart:
                showComingSoon();
                break;
            case R.id.ivShare:
                showComingSoon();
//                requestCameraWallpaper();
                break;
            case R.id.ivGift:
                showComingSoon();
                break;
            case R.id.ivGame:
                showComingSoon();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 11){
            if (resultCode == Activity.RESULT_OK){
                ToastUtils.getInstance(this).showMessage("Ok");
            }else {
                ToastUtils.getInstance(this).showMessage("cancle");
            }
        }
    }

    private void requestCameraWallpaper() {
        if (!Utils.hasPermissions(this, permission_camera)) {
            Utils.permission(this, permission_camera, KEY_REQUEST_CAMERA);
        } else {
            cameraWallpaper();
        }
    }

    private void cameraWallpaper() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this,
                CameraWallpaperService.class));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case KEY_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraWallpaper();

                } else {
                    ToastUtils.getInstance(this).showMessage(getString(R.string.need_permission));
                }
                return;
            }
        }
    }

    private void showComingSoon() {
        ToastUtils.getInstance(this).showMessage("Coming soon");
    }

    public static void nextChoseLiveWallpaper(Activity paramActivity, Class<?> paramClass) {
        try {
            Intent localIntent1 = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
            localIntent1.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT",
                    new ComponentName(paramActivity, paramClass));
            paramActivity.startActivity(localIntent1);
            return;
        } catch (Exception localException1) {
            try {
                Intent localIntent2 = new Intent();
                localIntent2.setAction("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
                paramActivity.startActivityForResult(localIntent2, 1);
                return;
            } catch (Exception localException2) {
            }
        }
    }
}
