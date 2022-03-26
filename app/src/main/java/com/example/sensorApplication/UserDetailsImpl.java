package com.example.sensorApplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDetailsImpl {
    @Query("SELECT COUNT(*) FROM UserDetails")
    public int count();

    //Get latest data row
    @Query("SELECT * FROM UserDetails where timestamp=(SELECT MAX(timestamp) FROM UserDetails)")
    public UserDetails getLatestData();

//    @Query("SELECT * FROM UserInfo")
//    public ArrayList<String> getData();

    @Insert
    public long insert(UserDetails userInfo);

    @Update
    public int update(UserDetails userInfo);
}
