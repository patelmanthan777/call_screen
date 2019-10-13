package com.mgosu.callscreen.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.network.ApiClient;
import com.mgosu.callscreen.data.api.wallpaper.image.tabLayout.DataImage;
import com.mgosu.callscreen.data.api.wallpaper.image.tabLayout.ObjDataImage;
import com.mgosu.callscreen.data.api.wallpaper.image.tabLayout.ObjList;
import com.mgosu.callscreen.databinding.FragmentTabBinding;
import com.mgosu.callscreen.ui.adapter.PagerAdapter;
import com.mgosu.callscreen.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentImage extends Fragment {

    private List<ObjList> list = new ArrayList<>();
    private FragmentTabBinding binding;
    private PagerAdapter adapter;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab, container, false);
        View view = binding.getRoot();
        binding.myButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataImage();
            }
        });
        getDataImage();
        return view;
    }


    private void createTabLayout() {
        for (ObjList objList : list) {
            binding.tabLayoutImage.addTab(binding.tabLayoutImage.newTab().setText(objList.getTitle()));
        }
        binding.tabLayoutImage.setTabGravity(TabLayout.GRAVITY_CENTER);
        binding.tabLayoutImage.setTabMode(TabLayout.MODE_SCROLLABLE);
        adapter = new PagerAdapter(getChildFragmentManager(), list.size(), list, true, false);
        binding.viewPagerImage.setAdapter(adapter);
        binding.viewPagerImage.setOffscreenPageLimit(list.size());
        binding.tabLayoutImage.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPagerImage));
        binding.viewPagerImage.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayoutImage));

    }

    private void getDataImage() {
        if (!isLoading) {
            isLoading = true;
            binding.setLoadingState(Constants.STATE_LOADING_MORE);
            Call<ObjDataImage> call = ApiClient.getsInstance().getClient().callDataTab("list_cat_wallpaper", 1);
            call.enqueue(new Callback<ObjDataImage>() {
                @Override
                public void onResponse(Call<ObjDataImage> call, Response<ObjDataImage> response) {
                    ObjDataImage body = response.body();
                    if (body != null) {
                        DataImage data = body.getData();
                        if (data != null) {
                            list = data.getList();
                            createTabLayout();
                        } else {
                            binding.setLoadingState(Constants.STATE_FAILED);
                        }
                        if (list.size() == 0) {
                            binding.setLoadingState(Constants.STATE_FAILED);
                        } else {
                            binding.setLoadingState(Constants.STATE_SUCCESS);
                        }
                        isLoading = false;
                    }


                }

                @Override
                public void onFailure(Call<ObjDataImage> call, Throwable t) {
                    isLoading = false;
                    if (list.size() == 0) {
                        binding.setLoadingState(Constants.STATE_FAILED);
                    } else {
                        binding.setLoadingState(Constants.STATE_SUCCESS);
                    }
                }
            });
        }

    }
}
