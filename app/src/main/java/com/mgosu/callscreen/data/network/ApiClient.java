package com.mgosu.callscreen.data.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.200.216/";
    private static ApiClient sInstance;
    private ApiSever apiSever;

    private ApiClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiSever = retrofit.create(ApiSever.class);
    }

    public ApiSever getClient() {
        return apiSever;
    }

    public static ApiClient getsInstance() {
        if (sInstance == null) sInstance = new ApiClient();
        return sInstance;
    }
}
