
package com.mgosu.callscreen.data.api.callsplash;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class CallFlash implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int Id;
    @ColumnInfo(name = "type")
    private String type;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("thumb_sm")
    @Expose
    private String thumbSm;
    @SerializedName("thumb_large")
    @Expose
    private String thumbLarge;
    @SerializedName("flash_url")
    @Expose
    private String flashUrl;
    @ColumnInfo(name = "love_count")
    @SerializedName("love_count")
    @Expose
    private int loveCount;
    @ColumnInfo(name = "download")
    @SerializedName("download")
    @Expose
    private int download;

    @ColumnInfo(name = "id_image")
    private int id_image;


    public CallFlash( String type, int loveCount, int download, int id_image) {

        this.type = type;
        this.loveCount = loveCount;
        this.download = download;
        this.id_image = id_image;
    }

    @Ignore
    public CallFlash() {
    }

    public int getId_image() {
        return id_image;
    }

    public void setId_image(int id_image) {
        this.id_image = id_image;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

   public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getThumbSm() {
        return thumbSm;
    }

    public void setThumbSm(String thumbSm) {
        this.thumbSm = thumbSm;
    }

    public String getThumbLarge() {
        return thumbLarge;
    }

    public void setThumbLarge(String thumbLarge) {
        this.thumbLarge = thumbLarge;
    }

    public String getFlashUrl() {
        return flashUrl;
    }

    public void setFlashUrl(String flashUrl) {
        this.flashUrl = flashUrl;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }
}
