package com.mgosu.callscreen.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.network.ApiClient;
import com.mgosu.callscreen.data.api.callsplash.DataCallFlash;
import com.mgosu.callscreen.data.api.callsplash.ObjCallFlash;
import com.mgosu.callscreen.databinding.CallFlashFragmentBinding;
import com.mgosu.callscreen.listener.OnListItemClickListener;
import com.mgosu.callscreen.ui.activity.DetailCallFlashActivity;
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

import static android.support.constraint.Constraints.TAG;
import static com.mgosu.callscreen.util.Constants.ITEM_PER_PAGE;
import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.STATE_LOADING;

public class FragmentPopular extends Fragment implements OnListItemClickListener {

    private List<CallFlash> listDataUrl;
    private int[] imageArray = {R.drawable.image_people1, R.drawable.image_people2,
            R.drawable.image_people3, R.drawable.image_people4, R.drawable.image_people5};
    private CallFlashAdapter adapter;
    private CallFlashFragmentBinding binding;
    private String subUrl;
    private int page = 1;
    private boolean isLoading = false;
    private GridLayoutManager gridLayoutManager;
    private int totalPage, vissibleThreshold = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.call_flash_fragment, container, false);
        View view = binding.getRoot();
        initView();
        loadData(Constants.STATE_LOADING);
//        callServer();
        return view;
    }

    private void loadData(int state) {
        if (!isLoading) {
            isLoading = true;
            binding.setLoadingState(state);
            Call<ObjCallFlash> callApi = ApiClient.getsInstance().getClient().getDataCallFlash("call_flash", "hot", page, ITEM_PER_PAGE);
            callApi.enqueue(new Callback<ObjCallFlash>() {
                @Override
                public void onResponse(Call<ObjCallFlash> call, Response<ObjCallFlash> response) {
                    ObjCallFlash body = response.body();
                    if (body != null) {
                        DataCallFlash data = body.getData();
                        adapter.addItemCall(data);
                        page = data.getCurrentPage() + 1;
                        totalPage = data.getTotalPage();
                    } else {
                        binding.setLoadingState(Constants.STATE_FAILED);
                    }
                    if (listDataUrl.size() == 0) {
                        binding.setLoadingState(Constants.STATE_FAILED);
                    } else {
                        binding.setLoadingState(Constants.STATE_SUCCESS);
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(Call<ObjCallFlash> call, Throwable t) {
                    isLoading = false;
                    if (listDataUrl.size() == 0) {
                        binding.setLoadingState(Constants.STATE_FAILED);
                    } else {
                        binding.setLoadingState(Constants.STATE_SUCCESS);
                    }
                }
            });
        }
    }

    private void initView() {
        listDataUrl = new ArrayList<>();
        binding.myButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(STATE_LOADING);
            }
        });
        adapter = new CallFlashAdapter(getContext(), listDataUrl);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerview.setLayoutManager(gridLayoutManager);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
        adapter.setOnListItemClickListener(FragmentPopular.this);
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCOunt = gridLayoutManager.getItemCount();
                int lastVissibleItemPOs = gridLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && (totalItemCOunt <= lastVissibleItemPOs + vissibleThreshold) && listDataUrl.size() >= ITEM_PER_PAGE && page > 1 && page <= totalPage) {
                    loadData(Constants.STATE_LOADING_MORE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtils.getInstance(getContext()).getCheckChange()) {
            PreferencesUtils.getInstance(getContext()).setCheckChange(false);
            Call<ObjCallFlash> call = ApiClient.getsInstance().getClient().getDataCallFlash("call_flash", "hot", 1, 1000);
            call.enqueue(new Callback<ObjCallFlash>() {
                @Override
                public void onResponse(Call<ObjCallFlash> call, Response<ObjCallFlash> response) {
                    ObjCallFlash body = response.body();
                    if (body != null) {
                        Log.e(TAG, "onResponse: ");
                        listDataUrl.clear();
                        DataCallFlash data = body.getData();
                        listDataUrl.addAll(data.getList());
                        String subUrl = data.getSubUrl();
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ObjCallFlash> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onClick(int pos) {
        if (Utils.isNetworkConnected(getContext())) {
            Intent intent = new Intent(getActivity(), DetailCallFlashActivity.class);
            intent.putExtra(KEY_SEND_OBJ, listDataUrl.get(pos));
            startActivity(intent);
        } else {
            ToastUtils.getInstance(getContext()).showMessage(getString(R.string.access_internet));
        }

    }
}