
package com.mgosu.callscreen.data.api.wallpaper.image.tabLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjList {

    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("title")
    @Expose
    private String title;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
