package com.example.todo.common

import kotlin.random.Random

fun getRandNum(from: Int = 0, to: Int = 0) : Int {
    return Random.nextInt(to - from) + from
}