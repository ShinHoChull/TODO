package com.example.todo.common

import android.util.Log

class Defines {

    companion object {
        public fun log(
            log : String ,
            logName : String = "YONG_CHEOL"
        ) {
            Log.d(logName , "==================")
            Log.d(logName , log)
            Log.d(logName , "=====================")
        }
    }

}