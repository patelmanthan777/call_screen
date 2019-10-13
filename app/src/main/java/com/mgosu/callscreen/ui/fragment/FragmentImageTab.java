package com.mgosu.callscreen.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.data.network.ApiClient;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ListDataImage3d;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ObjData3d;
import com.mgosu.callscreen.databinding.TabImageFragmentBinding;
import com.mgosu.callscreen.listener.OnListItemClickListener;
import com.mgosu.callscreen.ui.activity.DetailWallpaperActivity;
import com.mgosu.callscreen.ui.adapter.CallFlashAdapter;
import com.mgosu.callscreen.util.Constants;
import com.mgosu.callscreen.util.PreferencesUtils;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mgosu.callscreen.util.Constants.ITEM_PER_PAGE;
import static com.mgosu.callscreen.util.Constants.KEY_FRAGMENT;
import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.NAME_REQUEST;
import static com.mgosu.callscreen.util.Constants.STATE_LOADING;

public class FragmentImageTab extends Fragment implements OnListItemClickListener {
    private TabImageFragmentBinding binding;
    private String cat_wallpaper;
    private CallFlashAdapter adapter;
    private List<WallPaper> list = new ArrayList<>();
    private int page = 1;
    private boolean isLoading = false;
    private GridLayoutManager gridLayoutManager;
    private int totalPage, vissibleThreshold = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tab_image_fragment, container, false);
        View view = binding.getRoot();
        cat_wallpaper = getArguments().getString(KEY_FRAGMENT);
        initView();
        loadData(Constants.STATE_LOADING);
        return view;
    }

    private void initView() {
        list = new ArrayList<>();
        binding.myButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(STATE_LOADING);
            }
        });
        adapter = new CallFlashAdapter(getContext(), list, true);
        adapter.setOnListItemClickListener(this);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerViewImageTab.setLayoutManager(gridLayoutManager);
        binding.recyclerViewImageTab.setHasFixedSize(true);
        binding.recyclerViewImageTab.setAdapter(adapter);
        binding.recyclerViewImageTab.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCOunt = gridLayoutManager.getItemCount();
                int lastVissibleItemPOs = gridLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && (totalItemCOunt <= lastVissibleItemPOs + vissibleThreshold) && list.size() >= ITEM_PER_PAGE && page > 1 && page <= totalPage) {
                    loadData(Constants.STATE_LOADING_MORE);
                }
            }
        });
    }

    private void loadData(int state) {
        if (!isLoading) {
            isLoading = true;
            binding.setLoadingState(state);
            Call<ObjData3d> callApi = ApiClient.getsInstance().getClient().callDataWallpaper("list_items_wallpaper", cat_wallpaper, page, ITEM_PER_PAGE);
            callApi.enqueue(new Callback<ObjData3d>() {
                @Override
                public void onResponse(Call<ObjData3d> call, Response<ObjData3d> response) {
                    ObjData3d body = response.body();
                    if (body != null) {
                        ListDataImage3d data = body.getData();
                        adapter.addItemWallpaper(data);
                        page = data.getCurrentPage() + 1;
                        totalPage = data.getTotalPage();
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

                @Override
                public void onFailure(Call<ObjData3d> call, Throwable t) {
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
    public static FragmentImageTab newInstance(String someInt) {
        FragmentImageTab myFragment = new FragmentImageTab();
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT, someInt);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtils.getInstance(getContext()).getCheckChange()) {
            if (cat_wallpaper.equalsIgnoreCase(PreferencesUtils.getInstance(getContext()).getKeyRequestChange())) {
                cat_wallpaper = PreferencesUtils.getInstance(getContext()).getKeyRequestChange();
                Call<ObjData3d> call = ApiClient.getsInstance().getClient().callDataWallpaper("list_items_wallpaper", cat_wallpaper, 1, 20);
                call.enqueue(new Callback<ObjData3d>() {
                    @Override
                    public void onResponse(Call<ObjData3d> call, Response<ObjData3d> response) {
                        ObjData3d body = response.body();
                        if (body != null) {
                            ListDataImage3d data = body.getData();
                            if (data != null) {
                                list.clear();
                                list.addAll(data.getWallPapers());
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        PreferencesUtils.getInstance(getContext()).setCheckChange(false);
                    }

                    @Override
                    public void onFailure(Call<ObjData3d> call, Throwable t) {

                    }
                });
            }

        }
    }

    @Override
    public void onClick(int pos) {
        if (Utils.isNetworkConnected(getContext())) {
            Intent intent = new Intent(getActivity(), DetailWallpaperActivity.class);
            intent.putExtra(NAME_REQUEST, cat_wallpaper);
            intent.putExtra(KEY_SEND_OBJ, list.get(pos));
            startActivity(intent);
        } else {
            ToastUtils.getInstance(getContext()).showMessage(getString(R.string.access_internet));
        }
    }
}

