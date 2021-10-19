package com.example.todo.di.modules

import androidx.room.Room
import com.example.todo.R
import com.example.todo.model.todo_db.TodoDatabase
import com.example.todo.repository.TodoRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val roomModule = module {

    single {
        Room.databaseBuilder(
            androidApplication()
        ,   TodoDatabase::class.java
        ,   androidApplication().getString(R.string.database)
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<TodoDatabase>().todoDao() }

    single { TodoRepository(get()) }

}