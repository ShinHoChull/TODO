package com.example.todo

import android.app.Application
import com.example.todo.di.modules.activityModule
import com.example.todo.di.modules.apiModule
import com.example.todo.di.modules.appModule
import com.example.todo.di.modules.roomModule
import com.example.todo.vm.AViewModel
import com.example.todo.vm.BViewModel
import org.junit.Rule
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.get

abstract class AbstractKoinTest : KoinTest  {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            activityModule
        )
        modules(roomModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create {
            clazz-> Mockito.mock(clazz.java)
    }


}