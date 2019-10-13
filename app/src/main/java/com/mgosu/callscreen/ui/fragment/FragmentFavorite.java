package com.mgosu.callscreen.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.data.roomdatabase.RoomDb;
import com.mgosu.callscreen.databinding.FragmentLocalBinding;
import com.mgosu.callscreen.listener.OnListItemClickListener;
import com.mgosu.callscreen.ui.activity.DetailCallFlashActivity;
import com.mgosu.callscreen.ui.activity.DetailWallpaperActivity;
import com.mgosu.callscreen.ui.adapter.CallFlashAdapter;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.mgosu.callscreen.util.Constants.KEY_FRAGMENT;
import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.NAME_REQUEST;

public class FragmentFavorite extends Fragment implements OnListItemClickListener {
    private RoomDb roomDb;
    private List<WallPaper> wallpaperSplashes = new ArrayList<>();
    private List<CallFlash> callSplashes = new ArrayList<>();
    private CallFlashAdapter adapter;
    private boolean isType = false;

    private FragmentLocalBinding binding;

    public static FragmentFavorite newInstance(boolean isType) {
        FragmentFavorite myFragment = new FragmentFavorite();
        Bundle args = new Bundle();
        args.putBoolean(KEY_FRAGMENT, isType);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_local, container, false);
        View view = binding.getRoot();
        isType = getArguments().getBoolean(KEY_FRAGMENT);
        roomDb = RoomDb.getInstance(getActivity());
        if (isType) {
            wallpaperSplashes = roomDb.wallpaperSplashDao().ListAllFavoriteWallpaper();
            if (wallpaperSplashes.size() == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
            adapter = new CallFlashAdapter(getContext(), wallpaperSplashes, true);
            adapter.setOnListItemClickListener(this);

        } else {
            callSplashes = roomDb.callFlashDao().ListAllCallSplash();
            if (callSplashes.size() == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }

            adapter = new CallFlashAdapter(getContext(), callSplashes);
            adapter.setOnListItemClickListener(this);
        }
        binding.recyclerViewFavoriteWallpaper.setHasFixedSize(true);
        binding.recyclerViewFavoriteWallpaper.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewFavoriteWallpaper.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isType) {
            wallpaperSplashes.clear();
            wallpaperSplashes.addAll(roomDb.wallpaperSplashDao().ListAllFavoriteWallpaper());
            if (wallpaperSplashes.size() == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }else {
                binding.tvEmpty.setVisibility(View.GONE);
            }
        } else {
            callSplashes.clear();
            List<CallFlash> c = roomDb.callFlashDao().ListAllCallSplash();
            callSplashes.addAll(c);
            if (callSplashes.size() == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }else {
                binding.tvEmpty.setVisibility(View.GONE);
            }

        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(int pos) {
        if (Utils.isNetworkConnected(getContext())) {
            if (isType) {
                Intent intent = new Intent(getActivity(), DetailWallpaperActivity.class);
                intent.putExtra(NAME_REQUEST, "");
                intent.putExtra(KEY_SEND_OBJ, wallpaperSplashes.get(pos));
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), DetailCallFlashActivity.class);
                intent.putExtra(KEY_SEND_OBJ, callSplashes.get(pos));
                startActivity(intent);
            }

        } else {
            ToastUtils.getInstance(getContext()).showMessage(getString(R.string.access_internet));
        }
    }
}
