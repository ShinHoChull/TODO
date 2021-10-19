package com.example.todo.di.modules

import com.example.todo.b.BFragment
import com.example.todo.base.BaseFragment
import com.example.todo.base.BaseViewModel
import com.example.todo.vm.AViewModel
import com.example.todo.vm.BViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {

    viewModel {
        AViewModel(get())
    }

}