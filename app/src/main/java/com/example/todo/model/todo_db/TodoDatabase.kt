package com.example.todo.model.todo_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.model.domain.Todo

@Database( entities = [Todo::class] , version = 1 , exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDAO
}