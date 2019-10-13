package com.mgosu.callscreen.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.data.model.DownloadStatus;
import com.mgosu.callscreen.data.roomdatabase.RoomDb;
import com.mgosu.callscreen.databinding.ActivityWallpaperDetailBinding;
import com.mgosu.callscreen.service.beautiful.WallpaperImage3dService1;
import com.mgosu.callscreen.service.beautiful.WallpaperImage3dService2;
import com.mgosu.callscreen.service.download.DownloadService;
import com.mgosu.callscreen.service.video.VideoWallpaperService1;
import com.mgosu.callscreen.service.video.VideoWallpaperService2;
import com.mgosu.callscreen.util.Constants;
import com.mgosu.callscreen.util.PreferencesUtils;
import com.mgosu.callscreen.util.RootPath;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.io.File;
import java.io.IOException;

import static com.mgosu.callscreen.util.Constants.KEY_SEND_OBJ;
import static com.mgosu.callscreen.util.Constants.REQUEST_CHANGE;

public class DetailWallpaperActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityWallpaperDetailBinding binding;
    private RoomDb roomDb;
    private Dialog progressCustom;
    private WallPaper wallPaper;
    private RootPath rootPath;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallpaper_detail);
        binding.myProgressBarLoading.setVisibility(View.VISIBLE);
        roomDb = RoomDb.getInstance(this);
        rootPath = RootPath.getInstance(this);
        createDialogProgress();
        getDataIntent();
        readDataRoom();
        initView();
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadService.KEY_ACTION));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                DownloadStatus downloadStatus =
                        (DownloadStatus) bundle.getSerializable(DownloadService.KEY_DATA);
                if (downloadStatus != null && downloadStatus.getItemId().equals(wallPaper.getItemId())) {
                    if (downloadStatus.isComplete()) {
                        updateDownloadButton();
                    } else {
                        binding.myProgressBar.setProgress(downloadStatus.getProgress());
                        binding.myTextViewDownload.setText(downloadStatus.getProgress() + "%");
                    }

                }
            }
        }
    };


    private void initView() {
        binding.myImageButtonBack.setOnClickListener(this);
        binding.myTextViewDownload.setOnClickListener(this);
        loadImage();
        binding.myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                binding.myVideoView.start();
            }
        });
    }

    private void createDialogProgress() {
        progressCustom = new Dialog(this);
        progressCustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressCustom.setCancelable(false);
        progressCustom.setContentView(R.layout.progress_custom);
    }

    private void readDataRoom() {
        WallPaper temp =
                roomDb.wallpaperSplashDao().getFavoriteWallpaperByItemId(wallPaper.getItemId());
        binding.myLikeButton.setLiked(temp != null);
        binding.myLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Utils.submit2Server(true, wallPaper.getItemId());
                roomDb.wallpaperSplashDao().insertWallpaperFavorite(wallPaper);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                roomDb.wallpaperSplashDao().deleteByItemId(wallPaper.getItemId());
            }
        });
    }

    private void updateDownloadButton() {
        binding.myProgressBar.setProgress(100);
        boolean fileIsExisting = localFile.exists();
        if (fileIsExisting) {
            binding.myTextViewDownload.setText(getString(R.string.set_wallpaper));
            if (wallPaper.getType().equals(Constants.VIDEO)) {
                binding.myVideoView.setVideoPath(localFile.getAbsolutePath());
                binding.myVideoView.seekTo(1);
                binding.myVideoView.start();
                binding.myVideoView.setVisibility(View.VISIBLE);
                binding.myImageViewThumb.setVisibility(View.GONE);
            } else {
                binding.myVideoView.setVisibility(View.GONE);
                binding.myImageViewThumb.setVisibility(View.VISIBLE);
            }
            binding.myProgressBarLoading.setVisibility(View.GONE);
        } else {
            binding.myTextViewDownload.setText(getString(R.string.download_for_free));
            binding.myVideoView.setVisibility(View.GONE);
            binding.myImageViewThumb.setVisibility(View.VISIBLE);
        }
    }


    private void loadImage() {
        Glide.with(this).load(wallPaper.getThumbLarge()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target, DataSource dataSource,
                                           boolean isFirstResource) {
                binding.myProgressBarLoading.setVisibility(View.GONE);
                return false;
            }
        }).into(binding.myImageViewThumb);

    }

    private void getDataIntent() {
        wallPaper = (WallPaper) getIntent().getSerializableExtra(KEY_SEND_OBJ);
        if (wallPaper.getType().equals(Constants.VIDEO)) {
            localFile =
                    new File(rootPath.getWallPaperFolder() + "/" + Utils.getFileNameFromPath(wallPaper.getFileUrl()));
        } else if (wallPaper.getType().equals(Constants.ZIP)) {
            String folderName = Utils.getFileNameFromPath(wallPaper.getFileUrl());
            if (folderName.indexOf(".") != -1) {
                folderName = folderName.substring(0, folderName.lastIndexOf("."));
            }
            localFile =
                    new File(rootPath.getWallPaperFolder() + "/" + folderName);
        } else {
            localFile =
                    new File(rootPath.getWallPaperFolder() + "/" + Utils.getFileNameFromPath(wallPaper.getThumbLarge()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myImageButtonBack:
                onBackPressed();
                break;
            case R.id.myTextViewDownload:
                if (localFile.exists()) {
                    setMyWallpaper();
                } else {
                    startDownLoad();
                }
                break;
        }
    }


    private void setMyWallpaper() {
        if (wallPaper.getType().equals(Constants.VIDEO)) {
            setVideoWallpaper();
        } else if (wallPaper.getType().equals(Constants.IMAGE)) {
            new SetWallpaperAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            set3DWallpaper();
        }
    }

    private void startDownLoad() {
        Intent intent = new Intent(this, DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DownloadService.KEY_DATA, wallPaper);
        bundle.putInt(DownloadService.KEY_TYPE, DownloadService.TYPE_WALLPAPER);
        intent.putExtras(bundle);
        startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setLockWallpaper() {
        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
        try {
            WallpaperManager.getInstance(this).setBitmap(bitmap, null, true,
                    WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setWallpaperImage() {
        Utils.setWallpaper(this, localFile.getAbsolutePath());
    }

    private void set3DWallpaper() {
        if (!Utils.isMyServiceRunning(WallpaperImage3dService1.class, this)) {
            PreferencesUtils.getInstance(this).setCur3DWallpaper(localFile.getAbsolutePath());
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, WallpaperImage3dService1.class));
            startActivityForResult(intent, REQUEST_CHANGE);
        } else {
            PreferencesUtils.getInstance(this).setCur3DWallpaper(localFile.getAbsolutePath());
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, WallpaperImage3dService2.class));
            startActivityForResult(intent, REQUEST_CHANGE);
        }


    }

    private void setVideoWallpaper() {
        PreferencesUtils.getInstance(this).setCurLiveWallpaper(localFile.getAbsolutePath());
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        if (!Utils.isMyServiceRunning(VideoWallpaperService2.class, this)) {
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, VideoWallpaperService2.class));
        } else {
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, VideoWallpaperService1.class));
        }
        startActivityForResult(intent, REQUEST_CHANGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.getInstance(this).showMessage(getString(R.string.success_wallpaper));
            }
//            }else {
//                ToastUtils.getInstance(this).showMessage(getString(R.string.fail_wallpaper));
//            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateDownloadButton();

    }

    private class SetWallpaperAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressCustom.show();
        }

        @Override
        protected Void doInBackground(Void... args) {
            setWallpaperImage();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLockWallpaper();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (progressCustom != null && progressCustom.isShowing()) {
                progressCustom.dismiss();
                ToastUtils.getInstance(DetailWallpaperActivity.this).showMessage(getString(R.string.success_wallpaper));
                finish();
            }
        }
    }
}
