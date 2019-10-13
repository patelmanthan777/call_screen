package com.mgosu.callscreen.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;
import com.mgosu.callscreen.data.network.ApiClient;
import com.mgosu.callscreen.data.sendrequest.ObjRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.Images.Media.insertImage;
import static com.mgosu.callscreen.util.Constants.PATH_VIDEO_FOLDER;

public class Utils {
    private static final String FULL_TIME = "_yyyy-MM-dd-HH-mm-ss";
    public static String progress;

    @SuppressLint("DefaultLocale")
    public static String convertTypeName() {
        String result;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int minutes = calendar.get(Calendar.MINUTE);
        int month = calendar.get(Calendar.MONTH);
        int yeah = calendar.get(Calendar.YEAR);
        int second = calendar.get(Calendar.SECOND);
        result = String.format("%02d_%02d_%02d_%02d_%02d_%02d", second, hour, minutes, date,
                month + 1, yeah);
        return result;
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static void clearBackground(Dialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    public static String getCallingTime(int time) {
        String result;
        int hour = time / 3600;
        int remainSecond = time % 3600;
        int minutes = remainSecond / 60;
        int second = remainSecond % 60;
        result = String.format("%02d:%02d", minutes, second);
        return result;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void rejectCall(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                ITelephony telephonyService;

                TelephonyManager telephonyManager = (TelephonyManager) context.
                        getSystemService(Context.TELEPHONY_SERVICE);

                Method getITelephony = telephonyManager
                        .getClass()
                        .getDeclaredMethod("getITelephony");

                getITelephony.setAccessible(true);

                telephonyService = (ITelephony) getITelephony.invoke(telephonyManager);

                telephonyService.endCall();

            } catch (Exception ignored) {
            }
        } else {

            try {
                Method method = Class.forName("android.os.ServiceManager")
                        .getMethod("getService", String.class);
                IBinder binder = null;
                binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony telephony = ITelephony.Stub.asInterface(binder);
                telephony.endCall();
            } catch (Exception e) {
            }
        }
    }

    public static boolean hasDrawOverlayOtherApps(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return true;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String convertTime(long time) {
        String result;
        result = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        return result;
    }

    public static String getJson(Context activity, int a) {
        String json = "";
        Resources resources = activity.getResources();
        InputStream inputStream = resources.openRawResource(a);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        json = builder.toString();

        return json;
    }

    public static void permissionOverLay(Activity context, int overRequest) {
        if (!Utils.hasDrawOverlayOtherApps(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(intent, overRequest);
        }
    }

    public static void permission(Context context, String[] PERMISSIONS, int REQUEST) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST);
            } else {

            }
        } else {

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void shareImage(Context context, Bitmap bitmap) {
        String bitmapPath = insertImage(context.getContentResolver(),
                bitmap, "image share", null);
        Uri uri = Uri.parse(bitmapPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/jpg");
        context.startActivity(Intent.createChooser(intent, "Choose in app"));
    }

    public static void createFolder() {
        File folder = new File(PATH_VIDEO_FOLDER);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
    }

    public static String createFolder(String path) {
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static int getRandom(int max, int min) {
        Random r = new Random();
        int i1 = r.nextInt(max - min) + min;
        return i1;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean setWallpaper(Context context, String pathFile) {
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(context);
        Bitmap bitmap = BitmapFactory.decodeFile(pathFile);
        try {
            myWallpaperManager.setBitmap(bitmap);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public static void submit2Server(boolean isLove, String itemId) {
        String type = isLove ? "love" : "download";
        Call<ObjRequest> call =
                ApiClient.getsInstance().getClient().callDataCallFlash(
                        "like_items_wallpaper", itemId, type);
        call.enqueue(new Callback<ObjRequest>() {
            @Override
            public void onResponse(Call<ObjRequest> call,
                                   Response<ObjRequest> response) {

            }

            @Override
            public void onFailure(Call<ObjRequest> call, Throwable t) {

            }
        });
    }

    public static String getFileNameFromPath(String url) {
        if (!url.contains("/")) {
            return null;
        }
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }

    public static String getFileExtension(String fileName) {
        if (!fileName.contains(".")) {
            return fileName;
        }
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }


    public static boolean unZip(String dirPath, String fileName) {
        try {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(dirPath + "/" + fileName)));
            ZipEntry ze;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(dirPath, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                int read;
                try {
                    while ((read = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, read);
                    }
                } finally {
                    fout.close();
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }
}