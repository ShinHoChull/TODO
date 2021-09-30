package com.example.todo.vm

import androidx.lifecycle.ViewModel
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines

class BViewModel : BaseViewModel() {


    fun callBackData() {
        Defines.log("hello~ B Fragment")
    }



}