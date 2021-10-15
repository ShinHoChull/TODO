package com.example.todo.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todo.base.BaseViewModel
import com.example.todo.common.MsgBox
import com.example.todo.extensions.Event
import com.example.todo.model.domain.Todo
import org.koin.core.inject

class AViewModel : BaseViewModel() {

    val msgBox : MsgBox by inject()

    private val _todoList = MutableLiveData<ArrayList<Todo>>(ArrayList())
    val todoList : LiveData<ArrayList<Todo>>
        get() = _todoList

    //할일 문구
    val todoStr = MutableLiveData<String>("")

    //문구체크.
    private val _isTextCheck = MutableLiveData<Boolean>()
    val isTextCheck : LiveData<Boolean> get() = _isTextCheck 


    private val _openEvent = MutableLiveData<Event<String>>()
    val openEvent: LiveData<Event<String>> get() = _openEvent


    fun onClickEvent(text: String) {

        if (text == "") {
            _isTextCheck.value = false
            return
        }

        val arr = _todoList.value?.clone() as ArrayList<Todo>
        //_todoList.value?.clear()

        arr.apply {
            add(Todo(arr.size.toLong()
                , todoStr.value
                , ""
                , ""))
        }
        _todoList.value = arr
        todoStr.value = ""

        _openEvent.value = Event(text)
    }
}


