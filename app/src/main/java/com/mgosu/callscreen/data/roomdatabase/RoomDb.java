package com.mgosu.callscreen.data.roomdatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mgosu.callscreen.data.api.callsplash.CallFlash;
import com.mgosu.callscreen.data.model.MyContact;
import com.mgosu.callscreen.data.roomdatabase.dao.CallSplashDao;
import com.mgosu.callscreen.data.roomdatabase.dao.ContactDao;
import com.mgosu.callscreen.data.roomdatabase.dao.WallpaperSplashDao;
import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;


@Database(entities = {CallFlash.class, MyContact.class, WallPaper.class}, version =1, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {
    private static final String DB_NAME = "flashscreen.db";

    public abstract CallSplashDao callFlashDao();

    public abstract WallpaperSplashDao wallpaperSplashDao();

    public abstract ContactDao contactDao();

    private static RoomDb instance;

    public static RoomDb getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, RoomDb.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

}
