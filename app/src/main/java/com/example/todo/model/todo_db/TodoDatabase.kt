package com.example.todo.model.todo_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.model.domain.GPS
import com.example.todo.model.domain.Todo
import com.example.todo.model.gps_db.GpsDAO
import com.example.todo.model.todo_db.TodoDAO

@Database( entities = [Todo::class] , version = 2 , exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDAO

}