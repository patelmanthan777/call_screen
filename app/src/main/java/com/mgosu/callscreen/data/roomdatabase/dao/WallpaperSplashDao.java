package com.mgosu.callscreen.data.roomdatabase.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mgosu.callscreen.data.api.wallpaper.Image3d.WallPaper;

import java.util.List;

@Dao
public interface WallpaperSplashDao {

    @Query("SELECT * FROM WallPaper")
    List<WallPaper> ListAllFavoriteWallpaper();

    @Query("SELECT * FROM WallPaper WHERE itemId = :itemId")
    WallPaper getFavoriteWallpaperByItemId(String itemId);

    @Query("DELETE FROM WallPaper WHERE itemId = :itemId")
    void deleteByItemId(String itemId);
    @Insert
    void insertWallpaperFavorite(WallPaper... wallpaperSplashes);

}
