package com.mgosu.callscreen.listener;

import java.io.File;

public interface OnResponse {
    void start();

    void failed();

    void success(File file);

    void onProgressUpdate(int value);
}
