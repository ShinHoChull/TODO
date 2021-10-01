package com.example.todo.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todo.b.BFragment
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines
import com.example.todo.model.domain.Todo

class BViewModel : BaseViewModel() {

    fun callBackData() {
        Defines.log("hello~ B Fragment")
    }



}