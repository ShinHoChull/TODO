package com.example.todo.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.extensions.Event
import com.example.todo.model.domain.Todo
import com.example.todo.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.inject
class AViewModel( val mRepository: TodoRepository ) : BaseViewModel() {

    val msgBox : MsgBox by inject()

    private val _todoList = MutableLiveData<ArrayList<Todo>>(ArrayList())
    val todoList : LiveData<ArrayList<Todo>>
        get() = _todoList

    //할일 문구
    val todoStr = MutableLiveData<String>("")

    //문구 체크.
    private val _isTextCheck = MutableLiveData<Boolean>(false)
    val isTextCheck : MutableLiveData<Boolean> get() = _isTextCheck

    private val _openEvent = MutableLiveData<Event<String>>()
    val openEvent: LiveData<Event<String>> get() = _openEvent


    fun onClickEvent(text: String) {

        if (text == "") {
            _isTextCheck.value = true
            return
        }

        _isTextCheck.value = false

        Todo(null
            , todoStr.value
            , ""
            , "").apply {

            _todoList.value?.add(this)

            GlobalScope.launch(Dispatchers.IO) {
                mRepository.insert(this@apply)
            }
        }

        todoStr.value = ""
        _openEvent.value = Event(text)
    }

    fun updateTodo(obj : Todo) {
        GlobalScope.launch(Dispatchers.IO) {
            mRepository.updateTodo(obj)
        }
    }

    fun getAllData() : List<Todo> {
        return mRepository.getAllTodo()
    }



}



