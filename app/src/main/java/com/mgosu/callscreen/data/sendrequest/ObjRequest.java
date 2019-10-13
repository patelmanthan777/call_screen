
package com.mgosu.callscreen.data.sendrequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjRequest {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("type")
    @Expose
    private String type;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
