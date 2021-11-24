package com.example.todo.model.gps_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todo.model.domain.GPS

@Dao
interface GpsDAO {

    @Query("select * from GPS")
    fun getAllGpsList() : List<GPS>

    @Query("Select * from GPS where todo_num = :id order by reg_date ASC")
    fun getGpsList(id : Long) : List<GPS>

    @Query("select * from GPS where todo_num = :id order by reg_date DESC limit 1")
    fun getGpsOne(id : Long) : GPS?

    @Insert
    fun insertGps(gps : GPS)

}