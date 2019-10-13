package com.mgosu.callscreen.data.roomdatabase.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mgosu.callscreen.data.model.MyContact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM MyContact")
    List<MyContact> listContact();

    @Query("SELECT * FROM MyContact WHERE id = :id")
    MyContact ContactById(int id);

    @Insert
    void insertContacts(MyContact... contacts);

    @Delete
    void deleteContacts(MyContact... contacts);

    @Update
    void updateContacts(MyContact... contacts);
}
