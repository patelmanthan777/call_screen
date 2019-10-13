package com.mgosu.callscreen.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.ActivitySettingBinding;
import com.mgosu.callscreen.util.PreferencesUtils;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ActivitySettingBinding binding;
    private PreferencesUtils preferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        preferencesUtils = PreferencesUtils.getInstance(this);
        binding.swColorPhone.setChecked(preferencesUtils.getStateCallFlashSetting());
        binding.swLedFlash.setChecked(preferencesUtils.getChangeSwLed());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        binding.swColorPhone.setOnCheckedChangeListener(this);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.swColorPhone:
                preferencesUtils.setStateCallFlashSetting(isChecked);
                break;
            case R.id.swAppDefau:
                break;
            case R.id.swLedFlash:
                preferencesUtils.setChangeSwLed(isChecked);
                break;
        }
    }
}
