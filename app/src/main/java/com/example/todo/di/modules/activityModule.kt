package com.example.todo.di.modules

import com.example.todo.vm.AViewModel
import com.example.todo.vm.BViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {

    viewModel {
        AViewModel()
    }

    viewModel {
        BViewModel()
    }
}