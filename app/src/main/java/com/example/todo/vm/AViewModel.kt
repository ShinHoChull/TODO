package com.example.todo.vm

import android.widget.EditText
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines
import com.example.todo.model.domain.Todo

class AViewModel : BaseViewModel() {

    private val _todoList = MutableLiveData<ArrayList<Todo>>()
    val todoList : LiveData<ArrayList<Todo>>
        get() = _todoList


    //할일 문구
    val todoStr = MutableLiveData<String>()

    //저장 버튼
    var onClickButton = MutableLiveData<Unit>()

    init {
        _todoList.value = ArrayList()
    }

    fun addTodoData () {

        if (todoStr.value != null) {
            val arr = _todoList.value?.clone() as ArrayList<Todo>
            _todoList.value?.clear()

            arr.apply {
                add(Todo(arr.size
                    , todoStr.value
                    , ""
                    , ""))
            }

            _todoList.value = arr
        }
        onClickButton.value = Unit
    }



}