package com.mgosu.callscreen.util;

import android.content.Context;

import java.io.File;

public class RootPath {
    private final String PATH = "Data";
    private final String CALLFLASH = "CallFlash";
    private final String WALLPAPER = "WallPaper";


    private static RootPath instance;
    private Context context;
    private File root;

    private RootPath(Context context) {
        this.context = context;
        root = new File(context.getFilesDir() + "/" + PATH);
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public static RootPath getInstance(Context context) {
        if (instance == null) {
            instance = new RootPath(context);
        }
        return instance;
    }

    public String getRootPath() {
        return context.getFilesDir() + "/" + PATH;
    }

    public File getFilePath() {
        return root;
    }

    public String getCallFlashFolder(){
        return root.getAbsolutePath()+"/"+CALLFLASH;
    }
    public String getWallPaperFolder(){
        return root.getAbsolutePath()+"/"+WALLPAPER;
    }

}
