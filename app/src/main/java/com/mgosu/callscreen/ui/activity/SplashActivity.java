package com.mgosu.callscreen.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.abc;
        binding.videoLoad.setVideoURI(Uri.parse(path));
        binding.videoLoad.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
        binding.videoLoad.start();
    }
}
