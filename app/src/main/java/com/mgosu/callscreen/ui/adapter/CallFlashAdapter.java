package com.mgosu.callscreen.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.api.callsplash.DataCallFlash;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ListDataImage3d;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.databinding.ItemFeaturedBinding;
import com.mgosu.callscreen.listener.OnListItemClickListener;
import com.mgosu.callscreen.util.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mgosu.callscreen.util.Constants.imageArray;

public class CallFlashAdapter extends RecyclerView.Adapter<CallFlashAdapter.viewHolder> {
    private Context context;
    private OnListItemClickListener onListItemClickListener;
    private boolean isType;
    private List<WallPaper> listWallpaper;
    private List<CallFlash> callFlashes;

    public CallFlashAdapter(Context context, List<CallFlash> callFlashes) {
        this.context = context;
        this.callFlashes = callFlashes;
        this.isType = false;
    }

    public CallFlashAdapter(Context context, List<WallPaper> list, boolean isType) {
        this.context = context;
        this.listWallpaper = list;
        this.isType = isType;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemFeaturedBinding itemFeaturedBinding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.item_featured, viewGroup, false);
        return new viewHolder(itemFeaturedBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, final int pos) {
        if (!isType) {
            viewHolder.civCallAns.setVisibility(View.VISIBLE);
            viewHolder.civCallEnd.setVisibility(View.VISIBLE);
            viewHolder.circleImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(Utils.getRandom(imageArray)).into(viewHolder.circleImageView);

        }
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onClick(pos);
                }
            }
        });
        String string;
        if (isType){
            string = listWallpaper.get(pos).getThumbSm();
        }else {
            string = callFlashes.get(pos).getThumbSm();
        }



        RequestOptions builder = new RequestOptions().placeholder(R.drawable.image_loading);
        Glide.with(context).load(string).apply(builder).into(viewHolder.imageView);

        if (!isType) {
            viewHolder.tvDownload.setText(String.valueOf(callFlashes.get(pos).getDownload()));
            viewHolder.tvHeart.setText(String.valueOf(callFlashes.get(pos).getLoveCount()));

        } else {
            viewHolder.tvDownload.setText(String.valueOf(listWallpaper.get(pos).getDownload()));
            viewHolder.tvHeart.setText(String.valueOf(listWallpaper.get(pos).getLoveCount()));
        }
    }

    public void addItemCall(DataCallFlash dataCallFlash) {
        if (dataCallFlash.getList() != null && !dataCallFlash.getList().isEmpty()) {
            for (CallFlash callFlash: dataCallFlash.getList()){
                callFlash.setThumbSm(dataCallFlash.getSubUrl() + callFlash.getThumbSm());
                callFlash.setThumbLarge(dataCallFlash.getSubUrl() + callFlash.getThumbLarge());
                if (callFlash.getFlashUrl() != null && !callFlash.getFlashUrl().isEmpty()) {
                    callFlash.setFlashUrl(dataCallFlash.getSubUrl() + callFlash.getFlashUrl());
                }
            }

            callFlashes.addAll(dataCallFlash.getList());
        }
        notifyDataSetChanged();
    }

    public void addItemWallpaper(ListDataImage3d dataImage3d) {
        if (dataImage3d.getWallPapers() != null && !dataImage3d.getWallPapers().isEmpty()) {
            for (WallPaper wallPaper : dataImage3d.getWallPapers()) {
                wallPaper.setThumbSm(dataImage3d.getSubUrl() + wallPaper.getThumbSm());
                wallPaper.setThumbLarge(dataImage3d.getSubUrl() + wallPaper.getThumbLarge());
                if (wallPaper.getFileUrl() != null && !wallPaper.getFileUrl().isEmpty()) {
                    wallPaper.setFileUrl(dataImage3d.getSubUrl() + wallPaper.getFileUrl());
                }
                listWallpaper.add(wallPaper);
            }

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (!isType) {
            return callFlashes.size();
        } else {
            return listWallpaper.size();
        }

    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView civCallEnd, civCallAns;
        RelativeLayout layout;
        ImageView imageView;
        TextView tvHeart, tvDownload;
        CircleImageView circleImageView;

        public viewHolder(@NonNull ItemFeaturedBinding itemView) {
            super(itemView.getRoot());
            imageView = itemView.imageView;
            layout = itemView.LayoutItemImage;
            tvHeart = itemView.tvHeart;
            tvDownload = itemView.tvDowload;
            circleImageView = itemView.profileImage;
            civCallEnd = itemView.crCallEnd;
            civCallAns = itemView.crCallAns;
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }
}
