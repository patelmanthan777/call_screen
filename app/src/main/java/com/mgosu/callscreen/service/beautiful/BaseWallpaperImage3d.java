package com.mgosu.callscreen.service.beautiful;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.mgosu.callscreen.util.PreferencesUtils;
import com.mgosu.callscreen.util.Utils;

import java.io.File;


public class BaseWallpaperImage3d extends WallpaperService {
    private final String TAG = "BaseWallpaperImage3d";
    private final float SCALE = 1.15f;
    private final float MOVE_INTERVAL = 0.03f;
    private final int LOOP = 1;
    private PreferencesUtils preferencesUtils;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private float mSensorX;
    private float mSensorY;
    private float[] olds;

    private boolean mVisible;
    private Canvas canvas;
    private int screenWidth = 720;
    private int screenHeight = 1280;

    private Bitmap bitmaps[];
    private int totalPngFiles = 0;
    private float posX[], posY[];


    @Override
    public void onCreate() {
        super.onCreate();
        preferencesUtils = PreferencesUtils.getInstance(this);
        olds = new float[2];
        olds[0] = 0;
        olds[1] = 0;

        File file = new File(preferencesUtils.getCur3DWallpaper());
        File[] childFiles = file.listFiles();
        boolean isContainJpg = false;

        for (int i = 0; i < childFiles.length; i++) {
            String fileName = Utils.getFileNameFromPath(childFiles[i].getAbsolutePath());
            String extension = Utils.getFileExtension(fileName);
            if (extension.equalsIgnoreCase("jpg")) {
                totalPngFiles++;
                isContainJpg = true;
            } else if (extension.equalsIgnoreCase("png")) {
                totalPngFiles++;
            }
        }
        bitmaps = new Bitmap[totalPngFiles];
        if (isContainJpg) {
            bitmaps[0] = BitmapFactory.decodeFile(file.getAbsolutePath() + File.separator + (1) +
                    ".jpg");
            for (int i = 1; i < totalPngFiles; i++) {
                Bitmap bitmap =
                        BitmapFactory.decodeFile(file.getAbsolutePath() + File.separator + (i) +
                                ".png");
                bitmaps[i] = bitmap;
            }
        } else {
            for (int i = 0; i < totalPngFiles; i++) {
                Bitmap bitmap =
                        BitmapFactory.decodeFile(file.getAbsolutePath() + File.separator + (1 + i) +
                                ".png");
                bitmaps[i] = bitmap;
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        return new LiveWall();
    }


    public class LiveWall extends Engine implements SensorEventListener {

        final Handler mHandler = new Handler();
        private final Runnable mDrawFrame = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            //update  the matrix variables
//            screenWidth = getDesiredMinimumWidth();
//            screenHeight = getDesiredMinimumHeight();
            resizeBitmap();
            posX = new float[bitmaps.length];
            posY = new float[bitmaps.length];

            for (int i = 0; i < bitmaps.length; i++) {
                posX[i] = (screenWidth - bitmaps[i].getWidth()) / 2;
                posY[i] = (screenHeight - bitmaps[i].getHeight()) / 2;

            }

            drawFrame();
            startSimulation();
        }

        private void resizeBitmap() {
            Matrix matrix = new Matrix();
            float ratioX = screenWidth * 1.0f / bitmaps[0].getWidth();
            float ratioY = screenHeight * 1.0f / bitmaps[0].getHeight();
            float ratio = Math.max(ratioX, ratioY);
            matrix.setScale(ratio * SCALE,
                    ratio * SCALE);


            if (bitmaps.length > 0) {
                for (int i = 0; i < bitmaps.length; i++) {
                    bitmaps[i] = Bitmap.createBitmap(bitmaps[i], 0, 0,
                            bitmaps[i].getWidth(), bitmaps[i].getHeight(), matrix, true);
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawFrame);
        }

        //called when varaible changed
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawFrame);
            }
        }

        //called when surface destroyed
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            stopSimulation();
            mHandler.removeCallbacks(mDrawFrame);
        }

        public void drawFrame() {
            //getting the surface holder
            final SurfaceHolder holder = getSurfaceHolder();
            canvas = null;
            try {
                canvas = holder.lockCanvas(); // get the canvas
                if (canvas != null) {
                    canvasDraw();
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            mHandler.removeCallbacks(mDrawFrame);
            if (mVisible) {
                mHandler.postDelayed(mDrawFrame, LOOP);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        public void startSimulation() {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }
    }


    public void canvasDraw() {
        float[] accels = new float[2];
        accels[0] = mSensorX;
        accels[1] = mSensorY;
        float[] temp = new float[2];
        temp[0] = olds[0];
        temp[1] = olds[1];
        accels = lowPass(accels, temp);
        for (int i = 0; i < bitmaps.length; i++) {
            posX[i] =
                    posX[i] - (MOVE_INTERVAL * bitmaps[i].getWidth()) * (accels[0] - olds[0]) / (5f * (i * 0.4f + 1));
            posY[i] =
                    posY[i] + ((MOVE_INTERVAL * bitmaps[i].getHeight()) * (accels[1] - olds[1]) / (5f * (i * 0.4f + 1)));
            canvas.drawBitmap(bitmaps[i], posX[i],
                    posY[i], null);
        }
        olds[0] = accels[0];
        olds[1] = accels[1];
    }

    private final float ALPHA = 0.25f;

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


}
