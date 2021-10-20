package com.example.todo.model.todo_db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todo.model.domain.Todo

@Dao
interface TodoDAO {

    @Query("Select * from Todo")
    fun getTodoList() : List<Todo>

    @Insert
    fun insertTodo(todoModel : Todo)

    @Update
    fun updateTodo(todoModel : Todo)

    @Delete
    fun deleteTodo(todoModel : Todo)

}