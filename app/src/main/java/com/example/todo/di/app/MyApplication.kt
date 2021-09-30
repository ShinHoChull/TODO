package com.example.todo.di.app

import android.app.Application
import com.example.todo.di.modules.activityModule
import com.leaf.android_mvvm_example.di.modules.appModule
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
        }
    }
}