package com.example.todo.model.gps_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.model.domain.GPS
import com.example.todo.model.domain.Todo
import com.example.todo.model.gps_db.GpsDAO
import com.example.todo.model.todo_db.TodoDAO

@Database( entities = [ GPS::class] , version = 3  , exportSchema = false)
abstract class GpsDatabase : RoomDatabase() {

    abstract fun gpsDao() : GpsDAO

}

