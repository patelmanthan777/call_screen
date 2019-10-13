
package com.mgosu.callscreen.data.api.callsplash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjCallFlash {

    @SerializedName("data")
    @Expose
    private DataCallFlash data;

    public DataCallFlash getData() {
        return data;
    }

    public void setData(DataCallFlash data) {
        this.data = data;
    }

}
