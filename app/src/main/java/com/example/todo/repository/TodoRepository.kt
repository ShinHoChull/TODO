package com.example.todo.repository

import androidx.lifecycle.LiveData
import com.example.todo.common.Defines
import com.example.todo.model.domain.Todo
import com.example.todo.model.todo_db.TodoDAO
import com.example.todo.model.todo_db.TodoDatabase

class TodoRepository(private val dao: TodoDAO ) {

    fun insert(obj : Todo) {
        dao.insertTodo(obj)
    }

    fun getAllTodo () : LiveData<List<Todo>> = dao.getTodoList()

    fun updateTodo (obj : Todo) = dao.updateTodo(obj)

    fun deleteTodo (obj : Todo) = dao.deleteTodo(obj)




}