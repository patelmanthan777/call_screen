
package com.mgosu.callscreen.data.api.callsplash;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
@Entity
public class DataCallFlash {
    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo(name = "fileName")
    @SerializedName("list")
    @Expose
    private List<CallFlash> list = null;
    @SerializedName("sub_url")
    @Expose
    private String subUrl;
    @SerializedName("current_page")
    @Expose
    private int currentPage;
    @SerializedName("total_page")
    @Expose
    private int totalPage;
    @SerializedName("limit")
    @Expose
    private int limit;

    public List<CallFlash> getList() {
        return list;
    }

    public void setList(List<CallFlash> list) {
        this.list = list;
    }

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
