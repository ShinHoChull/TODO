package com.example.todo.repository

import com.example.todo.AbstractKoinTest
import com.example.todo.di.modules.activityModule
import com.example.todo.di.modules.appModule
import com.example.todo.model.domain.Todo
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

import org.koin.test.inject

class TodoRepositoryTest : AbstractKoinTest() {

    val repository : TodoRepository by inject<TodoRepository>()
    lateinit var mArr : ArrayList<Todo>

    @Before
    fun setUp() {


        mArr = repository.getAllTodo() as ArrayList<Todo>

    }

    @After
    fun tearDown() {
    }

    @Test
    fun insert() {

        repository.insert(Todo(null , "UnitTest","",""))
        assertEquals(3 ,mArr.size)

    }

    @Test
    fun getAllTodo() {
        repository.getAllTodo()
    }

    @Test
    fun updateTodo() {

    }

    @Test
    fun deleteTodo() {
    }
}