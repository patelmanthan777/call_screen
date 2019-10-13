package com.mgosu.callscreen.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class MyContact {
    @PrimaryKey()
    private int id;
    @ColumnInfo(name = "video_url")
    private String video_url;

    @Ignore
    public MyContact() {
    }

    public MyContact(int id, String video_url) {
        this.id = id;
        this.video_url = video_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
