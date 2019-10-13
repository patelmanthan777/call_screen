
package com.mgosu.callscreen.data.api.wallpaper.image.tabLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjDataImage {

    @SerializedName("data")
    @Expose
    private DataImage data;

    public DataImage getData() {
        return data;
    }

    public void setData(DataImage data) {
        this.data = data;
    }

}
