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
import com.mgosu.callscreen.databinding.VideoFragmentBinding;
import com.mgosu.callscreen.ui.adapter.PagerAdapter;
import com.mgosu.callscreen.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentVideo extends Fragment {

    private VideoFragmentBinding binding;
    private List<ObjList> list = new ArrayList<>();
    private PagerAdapter adapter;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.video_fragment, container, false);
        View view = binding.getRoot();
        binding.myButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        getData();
        return view;
    }

    private void getData() {
        if (!isLoading) {
            isLoading = true;
            binding.setLoadingState(Constants.STATE_LOADING_MORE);
            Call<ObjDataImage> call = ApiClient.getsInstance().getClient().callDataTab("list_cat_wallpaper", 2);
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

    private void createTabLayout() {
        for (ObjList objList : list) {
            binding.tabFragmentVideo.addTab(binding.tabFragmentVideo.newTab().setText(objList.getTitle()));
        }
        binding.tabFragmentVideo.setTabGravity(TabLayout.GRAVITY_CENTER);
        binding.tabFragmentVideo.setTabMode(TabLayout.MODE_FIXED);
        adapter = new PagerAdapter(getChildFragmentManager(), list.size(), list, true, true);
        binding.viewPagerImage.setAdapter(adapter);
        binding.tabFragmentVideo.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPagerImage));
        binding.viewPagerImage.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabFragmentVideo));
    }
}
