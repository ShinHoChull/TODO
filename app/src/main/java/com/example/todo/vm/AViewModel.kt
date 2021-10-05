package com.example.todo.vm

import android.view.View
import android.widget.EditText
import androidx.databinding.InverseMethod
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.todo.b.BFragment
import com.example.todo.base.BaseFragment
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.databinding.FragmentBBinding
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
            add(Todo(arr.size
                , todoStr.value
                , ""
                , ""))
        }
        _todoList.value = arr
        todoStr.value = ""

        _openEvent.value = Event(text)
    }
}

inline fun <T> LiveData<Event<T>>.eventObserve(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observer<Event<T>> {
    val wrappedObserver = Observer<Event<T>> { t ->
        t.getContentIfNotHandled()?.let {
            onChanged.invoke(it)
        }
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}