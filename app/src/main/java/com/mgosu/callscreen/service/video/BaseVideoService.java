package com.mgosu.callscreen.service.video;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.mgosu.callscreen.util.PreferencesUtils;

public class BaseVideoService extends WallpaperService {
    private final String TAG = "service";

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();

    }

    class VideoEngine extends Engine {
        private MediaPlayer mediaPlayer;

        public VideoEngine() {
            super();
            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(PreferencesUtils.getInstance(BaseVideoService.this).getCurLiveWallpaper()));
            mediaPlayer.setLooping(true);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            mediaPlayer.setSurface(holder.getSurface());
            mediaPlayer.start();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

        }


        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {

            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }
}