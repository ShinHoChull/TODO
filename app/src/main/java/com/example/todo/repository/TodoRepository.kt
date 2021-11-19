package com.example.todo.repository

import com.example.todo.model.domain.GPS
import com.example.todo.model.domain.Todo
import com.example.todo.model.gps_db.GpsDAO
import com.example.todo.model.todo_db.TodoDAO

class TodoRepository(
    private val dao: TodoDAO) {

    fun insert(obj : Todo) {
        dao.insertTodo(obj)
    }

    fun getAllTodo () : List<Todo> = dao.getTodoList()

    fun updateTodo (obj : Todo) = dao.updateTodo(obj)

    fun deleteTodo (obj : Todo) = dao.deleteTodo(obj)



}