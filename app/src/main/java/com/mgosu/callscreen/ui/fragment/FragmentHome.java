package com.mgosu.callscreen.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.databinding.FragmentCallSplashBinding;
import com.mgosu.callscreen.listener.onBack;
import com.mgosu.callscreen.ui.adapter.PagerAdapter;

import static android.support.constraint.Constraints.TAG;

public class FragmentHome extends Fragment implements View.OnClickListener {
    private Context context;
    private onBack onback;
    private FragmentCallSplashBinding binding;

    public void setOnback(onBack onback) {
        this.onback = onback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_splash, container, false);
        View view = binding.getRoot();
        binding.RelativeLayoutPopular.setAlpha(0.5f);
        binding.RelativeLayoutFavorite.setAlpha(0.5f);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), 3, false);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setAdapter(adapter);
        binding.ivBackCallFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onback != null) {
                    onback.onBack();
                }
            }
        });
        binding.RelativeLayoutFavorite.setOnClickListener(this);
        binding.RelativeLayoutFeature.setOnClickListener(this);
        binding.RelativeLayoutPopular.setOnClickListener(this);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                Log.e(TAG, "onPageSelected: " + i);
                switch (i) {
                    case 0:
                        setControlView(i);
                        break;
                    case 1:
                        setControlView(i);
                        break;
                    case 2:
                        setControlView(i);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        return view;
    }

    private void setControlView(int isView) {
        if (isView == 0) {
            binding.RelativeLayoutFeature.setAlpha(1.0f);
            binding.lineFeature.setVisibility(View.VISIBLE);
            binding.linePopular.setVisibility(View.GONE);
            binding.RelativeLayoutPopular.setAlpha(0.5f);

            binding.RelativeLayoutFavorite.setAlpha(0.5f);
            binding.lineFavoriteHeart.setVisibility(View.INVISIBLE);
        } else if (isView == 1) {
            binding.RelativeLayoutFeature.setAlpha(0.5f);
            binding.lineFeature.setVisibility(View.INVISIBLE);
            binding.linePopular.setVisibility(View.VISIBLE);
            binding.RelativeLayoutPopular.setAlpha(1f);
            binding.RelativeLayoutFavorite.setAlpha(0.5f);
            binding.lineFavoriteHeart.setVisibility(View.INVISIBLE);
        } else {
            binding.RelativeLayoutFavorite.setAlpha(1f);
            binding.lineFavoriteHeart.setVisibility(View.VISIBLE);
            binding.RelativeLayoutFeature.setAlpha(0.5f);
            binding.lineFeature.setVisibility(View.INVISIBLE);
            binding.linePopular.setVisibility(View.INVISIBLE);
            binding.RelativeLayoutPopular.setAlpha(0.5f);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RelativeLayoutFeature:
                binding.viewPager.setCurrentItem(0);
                break;
            case R.id.RelativeLayoutPopular:
                binding.viewPager.setCurrentItem(1);
                break;
            case R.id.RelativeLayoutFavorite:
                binding.viewPager.setCurrentItem(2);
                break;
        }
    }
}
