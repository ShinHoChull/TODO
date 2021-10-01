package com.example.todo.vm


import com.example.todo.AbstractKoinTest
import org.junit.After
import org.junit.Assert

import org.junit.Test
import org.koin.test.inject


class AViewModelTest : AbstractKoinTest() {

    private val viewModel by inject<AViewModel>()


    @Test
    fun addTodoDataTrue() {

        viewModel.addTodoData("abc")
        Assert.assertEquals(viewModel.todoList.value?.size , 1)

    }
}