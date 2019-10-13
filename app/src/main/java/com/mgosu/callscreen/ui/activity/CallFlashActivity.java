package com.mgosu.callscreen.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mgosu.callscreen.listener.onBack;
import com.mgosu.callscreen.ui.fragment.FragmentHome;

public class CallFlashActivity extends AppCompatActivity {
    private static final int CONTENT_VIEW_ID = 10101010;
    private onBack onback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        if (savedInstanceState == null) {
            Fragment fragment = new FragmentHome();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(CONTENT_VIEW_ID, fragment).commit();
            ((FragmentHome) fragment).setOnback(new onBack() {
                @Override
                public void onBack() {
                    onBackPressed();
                }
            });
        }
    }
}
