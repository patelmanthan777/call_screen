
package com.mgosu.callscreen.data.api.wallpaper.Image3d;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListDataImage3d {

    @SerializedName("sub_url")
    @Expose
    private String subUrl;
    @SerializedName("list")
    @Expose
    private List<WallPaper> wallPapers = null;
    @SerializedName("current_page")
    @Expose
    private int currentPage;
    @SerializedName("total_page")
    @Expose
    private int totalPage;
    @SerializedName("limit")
    @Expose
    private int limit;

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }

    public List<WallPaper> getWallPapers() {
        return wallPapers;
    }

    public void setWallPapers(List<WallPaper> wallPapers) {
        this.wallPapers = wallPapers;
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
