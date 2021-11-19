package com.example.todo.di.modules

import androidx.room.Room
import com.example.todo.R
import com.example.todo.model.gps_db.GpsDatabase
import com.example.todo.model.todo_db.TodoDatabase
import com.example.todo.repository.GpsRepository
import com.example.todo.repository.TodoRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val roomModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            TodoDatabase::class.java,
            androidApplication().getString(R.string.database)
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<TodoDatabase>().todoDao() }
    single { TodoRepository(get()) }
}

val gpsRoomModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            GpsDatabase::class.java,
            androidApplication().getString(R.string.gpsDatabase)
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<GpsDatabase>().gpsDao() }
    single { GpsRepository(get()) }
}