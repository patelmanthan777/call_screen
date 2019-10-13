package com.mgosu.callscreen.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DownloadStatus implements Serializable {
    @SerializedName("downloadId")
    private int downloadId;
    @SerializedName("itemId")
    private String itemId;
    @SerializedName("progress")
    private int progress;
    @SerializedName("isComplete")
    private boolean isComplete;
    @SerializedName("isZip")
    private boolean isZip;

    @SerializedName("dirPath")
    private String dirPath;
    @SerializedName("fileName")
    private String fileName;

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isZip() {
        return isZip;
    }

    public void setZip(boolean zip) {
        isZip = zip;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
