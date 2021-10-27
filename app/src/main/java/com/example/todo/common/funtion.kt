package com.example.todo.common

import java.text.SimpleDateFormat
import kotlin.random.Random

fun getRandNum(from: Int = 0, to: Int = 0) : Int {
    return Random.nextInt(to - from) + from
}

fun getNowTimeToStr(formatStr : String = "yyyy-MM-dd hh:mm:ss" ) : String {
    return SimpleDateFormat(formatStr)
        .format(System.currentTimeMillis())
}