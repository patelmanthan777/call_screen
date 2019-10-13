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
import com.mgosu.callscreen.data.network.ApiClient;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ListDataImage3d;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ObjData3d;
import com.mgosu.callscreen.databinding.FragmentRecycleviewBinding;
import com.mgosu.callscreen.listener.OnListItemClickListener;
import com.mgosu.callscreen.ui.activity.DetailWallpaperActivity;
import com.mgosu.callscreen.ui.adapter.CallFlashAdapter;
import com.mgosu.callscreen.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mgosu.callscreen.util.Constants.ITEM_PER_PAGE;
import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.NAME_REQUEST;
import static com.mgosu.callscreen.util.Constants.STATE_LOADING;

public class FragmentImage3d extends Fragment  implements OnListItemClickListener {
    private FragmentRecycleviewBinding binding;

    private List<WallPaper> list = new ArrayList<>();
    private CallFlashAdapter adapter;
    private int page = 1;
    private boolean isLoading = false;
    private GridLayoutManager gridLayoutManager;
    private int totalPage, vissibleThreshold = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycleview, container, false);
        View view = binding.getRoot();
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
            binding.recyclerViewImage3d.setLayoutManager(gridLayoutManager);
            binding.recyclerViewImage3d.setHasFixedSize(true);
            binding.recyclerViewImage3d.setAdapter(adapter);
            binding.recyclerViewImage3d.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            Call<ObjData3d> callApi = ApiClient.getsInstance().getClient().callDataWallpaper("list_items_wallpaper", "3d", page, ITEM_PER_PAGE);
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
    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(getActivity(), DetailWallpaperActivity.class);
        intent.putExtra(KEY_SEND_OBJ, list.get(pos));
        startActivity(intent);
    }
}
