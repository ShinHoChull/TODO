package com.example.todo.model.domain

data class Todo(
    val id : Int? = null,
    val todoStr : String? = null,
    val regDate : String? = null,
    val updateDate : String? = null,
    val isCheck : Boolean = false
)
