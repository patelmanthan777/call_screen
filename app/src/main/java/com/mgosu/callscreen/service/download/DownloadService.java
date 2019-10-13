package com.mgosu.callscreen.service.download;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.mgosu.callscreen.R;
import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;
import com.mgosu.callscreen.data.model.DownloadStatus;
import com.mgosu.callscreen.util.Constants;
import com.mgosu.callscreen.util.RootPath;
import com.mgosu.callscreen.util.ToastUtils;
import com.mgosu.callscreen.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadService extends Service {
    private final String TAG = DownloadService.class.getSimpleName();

    public static final int TYPE_CALL_FLASH = 0;
    public static final int TYPE_WALLPAPER = 1;
    public static final String KEY_TYPE = "type";
    public static final String KEY_DATA = "data";

    public static final String KEY_ACTION = "com.mgosu.callscreen.service.StartDownload";

    private List<DownloadStatus> downloadStatuses = new ArrayList<>();
    private RootPath rootPath;


    @Override
    public void onCreate() {
        super.onCreate();
        rootPath = RootPath.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        }
        if (bundle != null) {
            int type = bundle.getInt(KEY_TYPE);
            switch (type) {
                case TYPE_CALL_FLASH:
                    CallFlash callFlash = (CallFlash) bundle.getSerializable(KEY_DATA);
                    startDownloadNewItem(callFlash.getItemId(), callFlash.getFlashUrl(),
                            rootPath.getCallFlashFolder(),
                            Utils.getFileNameFromPath(callFlash.getFlashUrl()), false);
                    break;
                case TYPE_WALLPAPER:
                    WallPaper wallPaper = (WallPaper) bundle.getSerializable(KEY_DATA);
                    if (wallPaper.getType().equals(Constants.VIDEO)) {
                        startDownloadNewItem(wallPaper.getItemId(), wallPaper.getFileUrl(),
                                rootPath.getWallPaperFolder(),
                                Utils.getFileNameFromPath(wallPaper.getFileUrl()), false);
                    } else if (wallPaper.getType().equals(Constants.ZIP)) {
                        String fileName = Utils.getFileNameFromPath(wallPaper.getFileUrl());
                        String destinationFolder;
                        if (fileName.contains(".")) {
                            destinationFolder = fileName.substring(0, fileName.lastIndexOf("."));
                        } else {
                            destinationFolder = fileName;
                        }
                        startDownloadNewItem(wallPaper.getItemId(), wallPaper.getFileUrl(),
                                rootPath.getWallPaperFolder() + File.separator + destinationFolder, fileName
                                , true);
                    } else {
                        startDownloadNewItem(wallPaper.getItemId(), wallPaper.getThumbLarge(),
                                rootPath.getWallPaperFolder(),
                                Utils.getFileNameFromPath(wallPaper.getThumbLarge()), false);
                    }

                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startDownloadNewItem(final String itemId, String url, final String dirPath,
                                      String fileName, boolean isZip) {
        if (downloadStatuses.size() > 0) {
            for (int i = 0; i < downloadStatuses.size(); i++) {
                if (downloadStatuses.get(i).getItemId().equals(itemId)) {
                    return;
                }
            }
        }
        final int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        for (DownloadStatus downloadStatus : downloadStatuses) {
                            if (downloadStatus.getItemId().equals(itemId)) {
                                downloadStatus.setProgress((int) progressPercent);
                                broadCast(downloadStatus);
                                break;
                            }
                        }

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        for (DownloadStatus downloadStatus : downloadStatuses) {
                            if (downloadStatus.getItemId().equals(itemId)) {
                                if (downloadStatus.isZip()) {
                                    UnzipTask unzipTask = new UnzipTask();
                                    unzipTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                            downloadStatus);
                                } else {
                                    downloadStatus.setComplete(true);
                                    broadCast(downloadStatus);
                                    downloadStatuses.remove(downloadStatus);
                                }

                                break;
                            }
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        ToastUtils.getInstance(DownloadService.this).showMessage(getString(R.string.download_failed));
                        for (DownloadStatus downloadStatus : downloadStatuses) {
                            if (downloadStatus.getItemId().equals(itemId)) {
                                downloadStatus.setComplete(true);
                                broadCast(downloadStatus);
                                downloadStatuses.remove(downloadStatus);
                                break;
                            }
                        }
                    }
                });

        final DownloadStatus downloadStatus = new DownloadStatus();
        downloadStatus.setDownloadId(downloadId);
        downloadStatus.setItemId(itemId);
        downloadStatus.setZip(isZip);
        downloadStatus.setDirPath(dirPath);
        downloadStatus.setFileName(fileName);
        downloadStatuses.add(downloadStatus);
    }

    private void broadCast(DownloadStatus downloadStatus) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DATA, downloadStatus);
        Intent intent = new Intent(KEY_ACTION);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private class UnzipTask extends AsyncTask<DownloadStatus, Void, DownloadStatus> {

        @Override
        protected DownloadStatus doInBackground(DownloadStatus... downloadStatuses) {
            DownloadStatus downloadStatus = downloadStatuses[0];
            if (Utils.unZip(downloadStatus.getDirPath(), downloadStatus.getFileName())) {
                return downloadStatus;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(DownloadStatus aDownloadStatus) {
            super.onPostExecute(aDownloadStatus);
            if (aDownloadStatus != null) {
                for (DownloadStatus downloadStatus : downloadStatuses) {
                    if (downloadStatus.getItemId().equals(aDownloadStatus.getItemId())) {
                        downloadStatus.setComplete(true);
                        broadCast(downloadStatus);
                        downloadStatuses.remove(downloadStatus);
                    }
                }

                File file = new File(aDownloadStatus.getDirPath() + File.separator + aDownloadStatus.getFileName());
                if (file.exists()) {
                    file.delete();
                }

            } else {
                ToastUtils.getInstance(DownloadService.this).showMessage(getString(R.string.download_failed));
            }


        }
    }


}
