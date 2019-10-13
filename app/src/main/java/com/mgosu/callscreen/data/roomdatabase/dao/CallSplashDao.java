package com.mgosu.callscreen.data.roomdatabase.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mgosu.callscreen.data.api.callsplash.CallFlash;

import java.util.List;

@Dao
public interface CallSplashDao {

    @Query("SELECT * FROM CallFlash")
    List<CallFlash> ListAllCallSplash();

    @Query("SELECT * FROM CallFlash WHERE itemId = :itemId")
    CallFlash getCallSplashByItemId(String itemId);

    @Query("DELETE FROM CallFlash WHERE itemId = :itemId")
    void deleteByItemId(String itemId);
    @Delete
    void deleteCallSplash(CallFlash... callFlash);

    @Insert
    void insertCallSplash(CallFlash... callSplashes);

}
