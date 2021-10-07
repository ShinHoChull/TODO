package com.example.todo.model.todo_db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.todo.model.domain.Todo

@Dao
interface TodoDAO {

    @Query("Select * from Todo")
    fun getTodoList() : LiveData<List<Todo>>
}