package com.leaf.android_mvvm_example.di.modules

import com.example.todo.common.MsgBox
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory {
        MsgBox(androidContext())
    }
}