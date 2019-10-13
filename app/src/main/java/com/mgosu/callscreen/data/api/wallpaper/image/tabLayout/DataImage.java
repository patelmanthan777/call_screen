
package com.mgosu.callscreen.data.api.wallpaper.image.tabLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataImage {

    @SerializedName("list")
    @Expose
    private java.util.List<ObjList> list = null;

    public java.util.List<ObjList> getList() {
        return list;
    }

    public void setList(java.util.List<ObjList> list) {
        this.list = list;
    }

}
