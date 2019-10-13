
package com.mgosu.callscreen.data.api.wallpaper.Image3d;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjData3d {

    @SerializedName("data")
    @Expose
    private ListDataImage3d data;

    public ListDataImage3d getData() {
        return data;
    }

    public void setData(ListDataImage3d data) {
        this.data = data;
    }

}
