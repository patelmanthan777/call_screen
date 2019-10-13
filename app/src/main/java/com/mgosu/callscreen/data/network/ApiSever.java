package com.mgosu.callscreen.data.network;


import com.mgosu.callscreen.data.api.callsplash.ObjCallFlash;
import com.mgosu.callscreen.data.sendrequest.ObjRequest;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.ObjData3d;
import com.mgosu.callscreen.data.api.wallpaper.image.tabLayout.ObjDataImage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSever {
        @GET("dev/call.php")
    Call<ObjCallFlash> getDataCallFlash(@Query("type") String type, @Query("sort") String sort,
                                        @Query("page") int page, @Query("limit") int limit);

        @GET("dev/call.php")
    Call<ObjRequest> callDataCallFlash(@Query("type") String type, @Query("item_id") String id,
                                       @Query("push_type") String push_type);

        @GET("dev/call.php")
    Call<ObjData3d> callDataWallpaper(@Query("type") String type,
                                      @Query("cat_wallpaper") String cat_wallpaper,
                                      @Query("page") int page, @Query("limit") int limit);

        @GET("dev/call.php")
    Call<ObjDataImage> callDataTab(@Query("type") String type,
                                   @Query("wallpaper_type") int wallpaper_type);
}
