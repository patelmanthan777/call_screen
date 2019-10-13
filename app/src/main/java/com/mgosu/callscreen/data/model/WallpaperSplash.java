package com.mgosu.callscreen.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WallpaperSplash {
    @PrimaryKey(autoGenerate = true)
    private int Id;
    @ColumnInfo(name = "fileName")
    private String fileName;
    @ColumnInfo(name = "urlThumb")
    private String urlThumb;
    @ColumnInfo(name = "urlDown")
    private String urlDown;
    @ColumnInfo(name = "type")
    private String type;

    public WallpaperSplash(String fileName, String urlThumb, String urlDown, String type) {
        this.fileName = fileName;
        this.urlThumb = urlThumb;
        this.urlDown = urlDown;
        this.type = type;
    }

    @Ignore
    public WallpaperSplash() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrlThumb() {
        return urlThumb;
    }

    public void setUrlThumb(String urlThumb) {
        this.urlThumb = urlThumb;
    }

    public String getUrlDown() {
        return urlDown;
    }

    public void setUrlDown(String urlDown) {
        this.urlDown = urlDown;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
