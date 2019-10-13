
package com.mgosu.callscreen.data.sendrequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataRequest {

    @SerializedName("data")
    @Expose
    private ObjRequest data;

    public ObjRequest getData() {
        return data;
    }

    public void setData(ObjRequest data) {
        this.data = data;
    }

}
