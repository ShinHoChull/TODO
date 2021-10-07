package com.example.todo.di.app

import android.app.Application
import com.example.todo.di.modules.activityModule
import com.example.todo.di.modules.appModule
import com.example.todo.di.modules.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)

            modules(
                activityModule
                , appModule
            )
            modules(roomModule)
        }
    }
}