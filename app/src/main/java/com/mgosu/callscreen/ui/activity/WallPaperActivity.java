package com.mgosu.callscreen.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.ActivityWallPaperBinding;
import com.mgosu.callscreen.ui.adapter.PagerAdapter;

public class WallPaperActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityWallPaperBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wall_paper);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), 4, true);
        binding.viewpagerWallpaper.setOffscreenPageLimit(4);
        binding.viewpagerWallpaper.setAdapter(adapter);
        binding.RlGroup3d.setOnClickListener(this);
        binding.RlGroupImage.setOnClickListener(this);
        binding.RlGroupVideo.setOnClickListener(this);
        binding.RlGroupFavorite.setOnClickListener(this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        binding.viewpagerWallpaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        setControlView(0);
                        break;
                    case 1:
                        setControlView(1);
                        break;
                    case 2:
                        setControlView(2);
                        break;
                    case 3:
                        setControlView(3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setControlView(int i) {
        if (i == 0) {
            binding.RlGroup3d.setAlpha(1f);
            binding.RlGroupFavorite.setAlpha(0.5f);
            binding.RlGroupImage.setAlpha(0.5f);
            binding.RlGroupVideo.setAlpha(0.5f);
            binding.dot3d.setVisibility(View.VISIBLE);
            binding.dotVideo.setVisibility(View.INVISIBLE);
            binding.dotImage.setVisibility(View.INVISIBLE);
            binding.dotFavorite.setVisibility(View.INVISIBLE);
        } else if (i == 1) {
            binding.RlGroup3d.setAlpha(0.5f);
            binding.RlGroupVideo.setAlpha(1f);
            binding.RlGroupImage.setAlpha(0.5f);
            binding.RlGroupFavorite.setAlpha(0.5f);
            binding.dot3d.setVisibility(View.INVISIBLE);
            binding.dotVideo.setVisibility(View.VISIBLE);
            binding.dotImage.setVisibility(View.INVISIBLE);
            binding.dotFavorite.setVisibility(View.INVISIBLE);
        } else if (i == 2) {
            binding.RlGroup3d.setAlpha(0.5f);
            binding.RlGroupVideo.setAlpha(0.5f);
            binding.RlGroupImage.setAlpha(1f);
            binding.RlGroupFavorite.setAlpha(0.5f);
            binding.dot3d.setVisibility(View.INVISIBLE);
            binding.dotVideo.setVisibility(View.INVISIBLE);
            binding.dotImage.setVisibility(View.VISIBLE);
            binding.dotFavorite.setVisibility(View.INVISIBLE);
        } else if (i == 3) {
            binding.RlGroup3d.setAlpha(0.5f);
            binding.RlGroupVideo.setAlpha(0.5f);
            binding.RlGroupImage.setAlpha(0.5f);
            binding.RlGroupFavorite.setAlpha(1f);
            binding.dot3d.setVisibility(View.INVISIBLE);
            binding.dotVideo.setVisibility(View.INVISIBLE);
            binding.dotImage.setVisibility(View.INVISIBLE);
            binding.dotFavorite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RlGroup3d:
                binding.viewpagerWallpaper.setCurrentItem(0);
                break;
            case R.id.RlGroupImage:
                binding.viewpagerWallpaper.setCurrentItem(2);
                break;
            case R.id.RlGroupVideo:
                binding.viewpagerWallpaper.setCurrentItem(1);
                break;
            case R.id.RlGroupFavorite:
                binding.viewpagerWallpaper.setCurrentItem(3);
                break;


        }
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
    protected void onResume() {
        super.onResume();
    }
}
